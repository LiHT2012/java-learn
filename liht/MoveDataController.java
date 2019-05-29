package com.dbcool.controller;

import static com.dbcool.util.Constants.*;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.KeyPair;
import com.dbcool.dao.ddb.DdbDao;
import com.dbcool.exception.GeneralException;
import com.dbcool.model.JsonData;
import com.dbcool.model.ddb.DdbJson;
import com.dbcool.model.ddb.table.prod.DatameSeniorData;
import com.dbcool.model.ddb.table.prod.ProdDdbSeniorData;
import com.dbcool.model.dto.request.DdbBatchParameter;
import com.dbcool.model.dto.request.DdbInnerBatchParameter;
import com.dbcool.service.DdbService;
import com.dbcool.service.MysqlServiceResult;
import com.dbcool.service.TwoDimCommonService;
import com.dbcool.util.JacksonUtil;

@EnableAutoConfiguration
@RestController
@RequestMapping("/dbc")
public class MoveDataController {
    
    @Autowired
    private TwoDimCommonService twoDimCommonService;
    @Autowired
    private DdbService ddbService;
    @Autowired
    private DdbDao ddbDao;
    private static Logger logger = Logger.getLogger(MoveDataController.class);
    
    private static int fileNum = 0;
    
    @RequestMapping(value = "/movebasedoc/datame" , method = RequestMethod.POST)
    public String moveBaseJsonToDatame(@RequestBody Map<String,Object> map) throws GeneralException, IOException, InterruptedException {
        List<String> baseIds = (List<String>) map.get("baseIds");
        MysqlServiceResult mysqlRet = twoDimCommonService.getOnlyFirstIdsOrSecondIds(DDB_TB_BASE, baseIds,
                DDB_TB_DOC, null, Arrays.asList(0));
        List<String> docIds = mysqlRet.getMysqlDataIds();
        if (docIds.isEmpty()) {
            return null;
        }
        int tempSize = 200000;
        int totalCount = docIds.size();
        int temp = 100;
        int start = 0;
//        while (start < totalCount) {
//            int page = tempSize/temp;
//            ThreadPoolExecutor excutor = new ThreadPoolExecutor(50, 100, 10L, TimeUnit.SECONDS, 
//                    new LinkedBlockingDeque<Runnable>((tempSize / temp)));
//            int j = start;
//            while (j < totalCount) {
//                int toIndex = 0;
//                if (j + temp < totalCount) {
//                    toIndex = j + temp;
//                } else {
//                    toIndex = totalCount;
//                }
//                List<String> subDocIds = new ArrayList<>(docIds.subList(j, toIndex));
//                excutor.execute(new BatchSwitchRes(DDB_TB_DOC, subDocIds, ddbDao, page));
//                j = j+ temp;
//            }
//            
//            start = start + tempSize;
//        }
        int page = totalCount/temp;
        ThreadPoolExecutor excutor = new ThreadPoolExecutor(50, 100, 10L, TimeUnit.SECONDS, 
                new LinkedBlockingDeque<Runnable>((totalCount / temp + 1)));
        int j = 0;
        while (j < totalCount) {
            int toIndex = 0;
            if (j + temp < totalCount) {
                toIndex = j + temp;
            } else {
                toIndex = totalCount;
            }
            List<String> subDocIds = new ArrayList<>(docIds.subList(j, toIndex));
            excutor.execute(new BatchSwitchRes(DDB_TB_DOC, subDocIds, ddbDao, page));
            j = j+ temp;
        }
        return null;
    }
    
    public static class BatchSwitchRes implements Runnable {
        private String resType;
        private List<String> resIdList;
        private DdbDao ddbDao;
        private static Integer index = 0;
        public BatchSwitchRes(String resType, List<String> resIdList, DdbDao ddbDao, int page) {
           this.resType = resType;
           this.resIdList = resIdList;
           this.ddbDao = ddbDao;
           this.index = page;
        }
        private List<Object> batchGetDocFromFactube(List<String> docIds) {
            Map<Class<?> , List<KeyPair>> itemsToGet = new HashMap<>();
            Class<? extends DdbJson> ddbJsonClass = ProdDdbSeniorData.class;
            
            List<KeyPair> keyPairs = new ArrayList<>();
            for (String docId : docIds) {
                KeyPair keyPair = new KeyPair();
                keyPair.setHashKey(docId);
                keyPairs.add(keyPair);
            }   
            itemsToGet.put(ddbJsonClass, keyPairs);
            Map<String, List<Object>> result = null;
            try {
                result = ddbDao.batchSelect(itemsToGet);
            } catch (Exception e) {
                logger.error("\n");
                logger.error("It went wrong when get : "+docIds);
                e.printStackTrace();
            }
            if (null == result) {
                try {
                    logger.error("It went wrong when get : "+docIds);
                    throw new Exception("");
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                return result.get(ProdDdbSeniorData.class.getAnnotation(DynamoDBTable.class).tableName());
            }
            return null;
        }
        @Override
        public void run() {
            System.out.println("Thread task start. " + Thread.currentThread().getId());
            List<Object> ddbJsonList = batchGetDocFromFactube(resIdList);
            if (null == ddbJsonList) {
                logger.error("It went wrong when get : "+resIdList);
                return;
                
            }
            List<JsonData> jsonDatas = new ArrayList<>(ddbJsonList.size());
            List<DdbJson> ddbs = new ArrayList<>(ddbJsonList.size());
            for (Object ddbJson : ddbJsonList) {
                Map<String, Object> map = JacksonUtil.objToObjByJson(ddbJson,Map.class);
                Object obj = map.get("value");
                JsonData data = JacksonUtil.json2Obj(obj.toString(), JsonData.class);//.objToObjByJson(obj, JsonData.class);
                if (null != data) {
                    jsonDatas.add(data);
                    DdbJson ddb;
                    try {
                        ddb = DatameSeniorData.class.newInstance();
                        ddb.setId((String) map.get("id"));
                        ddb.setValue(obj.toString());
//                        ddb = JacksonUtil.json2Obj(ddbJson.toString(),DatameSeniorData.class);
                        if (null != ddb) {
                            ddbs.add(ddb);
                        } else {
                            System.out.println(ddbJson);
                        }
                    } catch (InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                        System.out.println("转成ddbJson有误"+ddbJson);
                        //logger.error("转成ddbJson有误"+ddbJson);
                    }
                } else {
                    System.out.println(ddbJson);
                }
            }
            
            try {
                ddbDao.batchSave(ddbs);
                writeDbcCoreIndex(DDB_TB_DOC, jsonDatas);
            } catch (Exception e) {
                logger.error("\n");
                logger.error("\n");
                logger.error(JacksonUtil.obj2Json(jsonDatas));
                System.out.println(JacksonUtil.obj2Json(jsonDatas));
                System.out.println(e.getLocalizedMessage());
            }
            
            System.out.println("Thread task finished. " + index--);
            
        }
        
    }
    
    public static void writeDbcCoreIndex(String resType, List<JsonData> resDataList) {
        File outputFile = new File("/home/dbc-intern5/datame/" + (fileNum++) + ".json");
        if (!outputFile.exists()) {
            try {
                outputFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileOutputStream outStr = null;
        BufferedOutputStream outputBuffer = null;
        try {
            outStr = new FileOutputStream(outputFile);
            outputBuffer = new BufferedOutputStream(outStr);
        } catch (FileNotFoundException e1) {
        }

        Map<String, Object> dbcCoreIndex = new HashMap<>();
        String[] nameArray = new String[1];
        String[] ownerGroupArray = new String[] {"58451a0401a791e5000967a6"};
        
        try {
            for (JsonData data : resDataList) {
                dbcCoreIndex.put("id", data.getId());
                nameArray[0] = data.gainName("zh");
                dbcCoreIndex.put("resName", nameArray);
                dbcCoreIndex.put("resType", resType);
                dbcCoreIndex.put("ownerUlist", ownerGroupArray);
                outputBuffer.write(JacksonUtil.obj2Json(dbcCoreIndex).getBytes());
                outputBuffer.write(10);
            }
            outputBuffer.flush();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        try {
            outputBuffer.close();
            outStr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @RequestMapping(value = "/movebasedoc" , method = RequestMethod.POST)
    public void moveDocsJsonToDatame(@RequestBody Map<String,Object> param) throws GeneralException, IOException, InterruptedException {
        List<String> docIds = (List<String>) param.get("docIds");
        Map<Class<?> , List<KeyPair>> itemsToGet = new HashMap<>();
        Class<? extends DdbJson> ddbJsonClass = ProdDdbSeniorData.class;
        
        List<KeyPair> keyPairs = new ArrayList<>();
        for (String docId : docIds) {
            KeyPair keyPair = new KeyPair();
            keyPair.setHashKey(docId);
            keyPairs.add(keyPair);
        }   
        itemsToGet.put(ddbJsonClass, keyPairs);
        Map<String, List<Object>> result = null;
        result = ddbDao.batchSelect(itemsToGet);
        List<Object> ddbJsonList = result.get(ProdDdbSeniorData.class.getAnnotation(DynamoDBTable.class).tableName());
        List<JsonData> jsonDatas = new ArrayList<>(ddbJsonList.size());
        List<DdbJson> ddbs = new ArrayList<>(ddbJsonList.size());
        for (Object ddbJson : ddbJsonList) {
            Map<String, Object> map = JacksonUtil.objToObjByJson(ddbJson,Map.class);
            Object obj = map.get("value");
            JsonData data = JacksonUtil.json2Obj(obj.toString(), JsonData.class);//.objToObjByJson(obj, JsonData.class);
            if (null != data) {
                jsonDatas.add(data);
                DdbJson ddb;
                try {
                    ddb = DatameSeniorData.class.newInstance();
                    ddb.setId((String) map.get("id"));
                    ddb.setValue(obj.toString());
//                    ddb = JacksonUtil.json2Obj(ddbJson.toString(),DatameSeniorData.class);
                    if (null != ddb) {
                        ddbs.add(ddb);
                    } else {
                        System.out.println(ddbJson);
                    }
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                    System.out.println("转成ddbJson有误"+ddbJson);
                    //logger.error("转成ddbJson有误"+ddbJson);
                }
            } else {
                System.out.println(ddbJson);
            }
        }
        ddbDao.batchSave(ddbs);
        writeDbcCoreIndex(DDB_TB_DOC, jsonDatas);
    }
}

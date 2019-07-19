JacksonUtil：

//com.fasterxml.jackson.databind.ObjectMapper
private static ObjectMapper objectMapper = new ObjectMapper();

public static <T> T json2Obj(String json, Class<T> t) {
        objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        T obj = null;
        try {
            obj = objectMapper.readValue(json, t);
        } catch (Exception e) {
            logger.error(e);
            e.printStackTrace();
        }
        return obj;
    }

    public static String obj2Json(Object obj) {
        String json = null;
        try {
            json = objectMapper.writeValueAsString(obj);
        } catch (JsonGenerationException e) {
            logger.error(e);
            e.printStackTrace();
        } catch (JsonMappingException e) {
            logger.error(e);
            e.printStackTrace();
        } catch (IOException e) {
            logger.error(e);
            e.printStackTrace();
        }
        return json;
    }

JsonUtil:用gson

import com.dbcool.api.util.gsonadapter.MapDeserializerDoubleAsIntFix;
import com.dbcool.api.util.gsonadapter.MyDateDerAndSerializer;
import com.dbcool.api.util.gsonadapter.MyDoubleSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class JsonUtil {

    private static Gson gson = new GsonBuilder().serializeNulls()
            .registerTypeAdapter(Date.class, new MyDateDerAndSerializer())
            .registerTypeAdapter(new TypeToken<Map<String, Object>>() {
            }.getType(), new MapDeserializerDoubleAsIntFix())
            .registerTypeAdapter(Double.class, new MyDoubleSerializer())
            .create();

    public static Gson getGsonBean() {
        return gson;
    }

    public static Type stringlistType = new TypeToken<List<String>>(){}.getType();

    /**
     * json转java对象
     *
     * @param t
     */
    public static <T> T json2Obj(String json, Class<T> t) {
        return gson.fromJson(json, t);
    }
    /**
     * 要转化成集合类对象，调用次方法
     * @param json
     * @param typeOfT
     * @return
     */
    public static <T> T json2Obj(String json, Type typeOfT) {
        return gson.fromJson(json, typeOfT);
    }

    /**
     * java对象转json
     *
     * @param args
     */
    public static String obj2Json(Object obj) {
        return gson.toJson(obj);
    }

	public static List<String> json2StringList(String json) {
		return gson.fromJson(json, stringlistType);
	}
}

import com.google.gson.*;
import com.google.gson.internal.LinkedTreeMap;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MapDeserializerDoubleAsIntFix implements JsonDeserializer<Map<String, Object>> {

    @Override  @SuppressWarnings("unchecked")
    public Map<String, Object> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return (Map<String, Object>) read(json);
    }

    public Object read(JsonElement in) {

        if(in.isJsonArray()){
            List<Object> list = new ArrayList<Object>();
            JsonArray arr = in.getAsJsonArray();
            for (JsonElement anArr : arr) {
                list.add(read(anArr));
            }
            return list;
        }else if(in.isJsonObject()){
            Map<String, Object> map = new LinkedTreeMap<String, Object>();
            JsonObject obj = in.getAsJsonObject();
            Set<Map.Entry<String, JsonElement>> entitySet = obj.entrySet();
            for(Map.Entry<String, JsonElement> entry: entitySet){
                map.put(entry.getKey(), read(entry.getValue()));
            }
            return map;
        }else if( in.isJsonPrimitive()){
            JsonPrimitive prim = in.getAsJsonPrimitive();
            if(prim.isBoolean()){
                return prim.getAsBoolean();
            }else if(prim.isString()){
                return prim.getAsString();
            }else if(prim.isNumber()){
                Number num = prim.getAsNumber();
                // here you can handle double int/long values
                // and return any type you want
                // this solution will transform 3.0 float to long values
                if(Math.ceil(num.doubleValue())  == num.intValue())
                    return num.intValue();
                else{
                    return num.doubleValue();
                }
            }
        }
        return null;
    }
}

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Date;

public class MyDateDerAndSerializer implements JsonSerializer<Date> , JsonDeserializer<Date>{
    @Override
    public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
        if(null != src){
            return new JsonPrimitive(src.getTime());
        }
        return null;
    }

    @Override
    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonNull() || json.getAsString().length() == 0) {
            return null;
        }
        try {
            return new java.util.Date(json.getAsJsonPrimitive().getAsLong());
        } catch (Exception e) {
            return null;
        }    }
}

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class MyDoubleSerializer implements JsonSerializer<Double> {
    @Override
    public JsonElement serialize(Double src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == src.longValue())
            return new JsonPrimitive(src.longValue());
        return new JsonPrimitive(src);
    }
}


@SerializedName("uid")//obj转化json的时候 gson用
@JSONField(name = "uid")//fastjson用
protected String userId;//对应mysql中的字段user_id

compile group: 'com.google.code.gson', name: 'gson', version: '2.8.0'
compile('com.alibaba:fastjson:1.2.47')

对于@JSONField的生效，还需在启动类中implements WebMvcConfigurer并重写configureMessageConverters方法，具体如下：
public class Application extends SpringBootServletInitializer implements WebMvcConfigurer{
……

@Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(
                SerializerFeature.PrettyFormat,
                SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteNullNumberAsZero,
                SerializerFeature.WriteNullStringAsEmpty
            );
        // 处理中文乱码问题
        List<MediaType> fastMediaTypes = new ArrayList<>();
        fastMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        fastConverter.setSupportedMediaTypes(fastMediaTypes);
        fastConverter.setFastJsonConfig(fastJsonConfig);

        //处理字符串, 避免直接返回字符串的时候被添加了引号
        StringHttpMessageConverter smc = new StringHttpMessageConverter(Charset.forName("UTF-8"));
        converters.add(smc);

        converters.add(fastConverter);
    }

……
@Bean//没有配置成功，待再修改测试
    public HttpMessageConverters fastJsonHttpMessageConverters() {
        //1.需要定义一个Convert转换消息的对象
        FastJsonHttpMessageConverter fastConverter=new FastJsonHttpMessageConverter();
        //2.添加fastjson的配置信息，比如是否要格式化返回的json数据
        FastJsonConfig fastJsonConfig=new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat, SerializerFeature.DisableCircularReferenceDetect);
        //BigDecimal数据处理
        SerializeConfig serializeConfig = SerializeConfig.getGlobalInstance();
        serializeConfig.put(BigDecimal.class, CustomerBigDecimalCodec.instance);
        fastJsonConfig.setSerializeConfig(serializeConfig);
        //3.在convert中添加配置信息
        fastConverter.setFastJsonConfig(fastJsonConfig);
        //4.中文乱码
        List<MediaType> fastMediaTypes = new ArrayList<>();
        fastMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        fastConverter.setSupportedMediaTypes(fastMediaTypes);

        return new HttpMessageConverters(fastConverter);

    }
https://blog.csdn.net/qq_32967665/article/details/82941090
https://segmentfault.com/a/1190000015792733

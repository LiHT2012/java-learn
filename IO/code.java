public String uploadQianURL(String fileUrl) {
		// 获取文件名，文件名实际上在URL中可以找到
		String fileName = "test.jpg";
		// 这里服务器上要将此图保存的路径
		String savePath = "/home/dbc-intern5testPic/";
		try {
			URL url = new URL(fileUrl);/* 将网络资源地址传给,即赋值给url */
			/* 此为联系获得网络资源的固定格式用法，以便后面的in变量获得url截取网络资源的输入流 */
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			DataInputStream in = new DataInputStream(connection.getInputStream());
			/* 此处也可用BufferedInputStream与BufferedOutputStream */
			DataOutputStream out = new DataOutputStream(new FileOutputStream(savePath + fileName));
			/* 将参数savePath，即将截取的图片的存储在本地地址赋值给out输出流所指定的地址 */
			byte[] buffer = new byte[4096];
			int count = 0;
			/* 将输入流以字节的形式读取并写入buffer中 */
			while ((count = in.read(buffer)) > 0) {
				out.write(buffer, 0, count);
			}
			out.close();/* 后面三行为关闭输入输出流以及网络资源的固定格式 */
			in.close();
			connection.disconnect();
			// 返回内容是保存后的完整的URL
			return fileName;/* 网络资源截取并存储本地成功返回true */

		} catch (Exception e) {
			System.out.println(e + fileUrl + savePath);
			return null;
		}
}

private static void download(String url, String filepath) {
        OkHttpClient client = new OkHttpClient();
        System.out.println(url);
        Request req = new Request.Builder().url(url).build();
        Response resp = null;
        try {
            resp = client.newCall(req).execute();
            System.out.println(resp.isSuccessful());
            if(resp.isSuccessful()) {
                ResponseBody body = resp.body();
                InputStream is = body.byteStream();
                byte[] data = readInputStream(is);
                System.out.println(getFileFormat(data));
                FileOutputStream fops = new FileOutputStream(filepath + "123."+getFileFormat(data));
                fops.write(data);
                fops.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Unexpected code " + resp);
        }
}

/**
 * 读取字节输入流内容
 * @param is
 * @return
 */
private static byte[] readInputStream(InputStream is) {
    ByteArrayOutputStream writer = new ByteArrayOutputStream();
    byte[] buff = new byte[1024 * 2];
    int len = 0;
    try {
        while((len = is.read(buff)) != -1) {
            writer.write(buff, 0, len);
        }
        is.close();
    } catch (IOException e) {
        e.printStackTrace();
    }
    return writer.toByteArray();
}


public static String getFileFormat(byte[] bytes) {
	String type = "";
	ByteArrayInputStream bais = null;
	MemoryCacheImageInputStream mcis = null;
	try {
		bais = new ByteArrayInputStream(bytes);
		mcis = new MemoryCacheImageInputStream(bais);
		Iterator<ImageReader> itr = ImageIO.getImageReaders(mcis);
		while (itr.hasNext()) {
			ImageReader reader = (ImageReader) itr.next();
			String imageName = reader.getClass().getSimpleName();
			if (imageName != null) {
				if ("GIFImageReader".equals(imageName)) {
					type = "gif";
				} else if ("JPEGImageReader".equals(imageName)) {
					type = "jpg";
				} else if ("PNGImageReader".equals(imageName)) {
					type = "png";
				} else if ("BMPImageReader".equals(imageName)) {
					type = "bmp";
				} else {
					type = "noPic";
				}
			}
		}
	} finally {
		if (bais != null)
			try {
				bais.close();
			} catch (IOException ioe) {
			}
		if (mcis != null)
			try {
				mcis.close();
			} catch (IOException ioe) {
			}
	}
	return type;
}



FileOutputStream fileWriter = new FileOutputStream("/home/liht/桌面/test.text");
OutputStreamWriter writer = new OutputStreamWriter(fileWriter, "utf-8");
writer.write("manualId:");
writer.close();//其中包含 writer.flush();流程


//　读取文件，将文件中的数据一行行的取出。
//法一:通过BufferedReader的readLine()方法。
/**
 * 功能：Java读取txt文件的内容 步骤：1：先获得文件句柄 2：获得文件句柄当做是输入一个字节码流，需要对这个输入流进行读取
 * 3：读取到输入流后，需要读取生成字节流 4：一行一行的输出。readline()。 备注：需要考虑的是异常情况
 *
 * @param filePath
 *            文件路径[到达文件:如： D:\aa.txt]
 * @return 将这个文件按照每一行切割成数组存放到list中。
 */
public static List<String> readTxtFileIntoStringArrList(String filePath)
{
    List<String> list = new ArrayList<String>();
    try
    {
        String encoding = "GBK";
        File file = new File(filePath);
        if (file.isFile() && file.exists())
        { // 判断文件是否存在
            InputStreamReader read = new InputStreamReader(
                    new FileInputStream(file), encoding);// 考虑到编码格式
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTxt = null;

            while ((lineTxt = bufferedReader.readLine()) != null)
            {
                list.add(lineTxt);
            }
            bufferedReader.close();
            read.close();
        }
        else
        {
            System.out.println("找不到指定的文件");
        }
    }
    catch (Exception e)
    {
        System.out.println("读取文件内容出错");
        e.printStackTrace();
    }

    return list;
}
//方式2
//通过文件byte数组暂存文件中内容，将其转换为String数据，再按照 “回车换行” 进行分割。
/**
 * 读取filePath的文件，将文件中的数据按照行读取到String数组中
 * @param filePath    文件的路径
 * @return            文件中一行一行的数据
 */
public static String[] readToString(String filePath)
{
    File file = new File(filePath);
    Long filelength = file.length(); // 获取文件长度
    byte[] filecontent = new byte[filelength.intValue()];
    try
    {
        FileInputStream in = new FileInputStream(file);
        in.read(filecontent);
        in.close();
    } catch (FileNotFoundException e)
    {
        e.printStackTrace();
    } catch (IOException e)
    {
        e.printStackTrace();
    }

    String[] fileContentArr = new String(filecontent).split("\r\n");

    return fileContentArr;// 返回文件内容,默认编码
}
/**
	方式1是将文件的一部分或全部数据读取出来用BufferReader缓存起来，需要再冲缓存中取数据，这样比要得时候去文件中读取要快一些。

　方式2是一次把文本的原始内容直接读取到内存中再做处理（暂时不考虑内存大小），这样做效率也会提高。同时，可以处理当你使用第1方式用readLine()方法时，
文件又有线程在不断的向文件中写数据【只处理现在已经在文件中的数据】。另外，用readline()之类的方法，可能需要反复访问文件，而且每次readline()都会调用编码转换，
降低了速度，所以，在已知编码的情况下，按字节流方式先将文件都读入内存，再一次性编码转换是最快的方式。
**/

try {
	File file = new File(fileName);
	RandomAccessFile fileR = new RandomAccessFile(file,"r");//r/rw/rws/rwd,打开文件的模式，只读，读写等
	// 按行读取字符串
	String str = null;
	while ((str = fileR.readLine())!= null) {
	}
		fileR.close();
} catch (IOException e1) {
	e1.printStackTrace();
}

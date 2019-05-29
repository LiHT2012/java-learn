RestTemplate是Spring提供的用于访问Rest服务的客户端，RestTemplate提供了多种便捷访问远程Http服务的方法，能够大大提高客户端的编写效率。
    调用RestTemplate的默认构造函数，RestTemplate对象在底层通过使用java.net包下的实现创建HTTP 请求，可以通过使用ClientHttpRequestFactory指定不同的HTTP请求方式。
    ClientHttpRequestFactory接口主要提供了两种实现方式

        一种是SimpleClientHttpRequestFactory，使用J2SE提供的方式（既java.net包提供的方式）创建底层的Http请求连接。//默认使用，内部调用jdk的HttpConnection，默认超时为-1
        一种方式是使用HttpComponentsClientHttpRequestFactory方式，底层使用HttpClient访问远程的Http服务，使用HttpClient可以配置连接池和证书等信息。


如果只是临时的向一个网址进行请求，可以直接用RestTemplate，如：
 String res = new RestTemplate().getForObject(url, String.class);

若完整写远程调用，则写一个类创建需要的RestTemplate进行一些设置，如下：
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

public class RestClientFactory {
	private static final int  TIME_OUT_SECONDS = 200;
	public static RestTemplate createRestTemplate() {
		// 长连接保持30秒
        PoolingHttpClientConnectionManager pollingConnectionManager = new PoolingHttpClientConnectionManager(30, TimeUnit.SECONDS);
        // 总连接数
        pollingConnectionManager.setMaxTotal(1000);
        // 同路由的并发数
        pollingConnectionManager.setDefaultMaxPerRoute(1000);
        
        HttpClientBuilder httpClientBuilder = HttpClients.custom();
//        /**
//         * http headers
//         */
//        List<Header> headers = new ArrayList<>();
//        headers.add(new BasicHeader("Content-Type","application/x-www-form-urlencoded"));
//        httpClientBuilder.setDefaultHeaders(headers);
// 
        httpClientBuilder.setConnectionManager(pollingConnectionManager);
        // 重试次数，默认是3次，没有开启
        httpClientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(2, true));
        // 保持长连接配置，需要在头添加Keep-Alive
        httpClientBuilder.setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy());
        HttpClient httpClient = httpClientBuilder.build();
 
        // httpClient连接配置，底层是配置RequestConfig
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        // 连接超时
//        clientHttpRequestFactory.setConnectTimeout(5000);
        
        clientHttpRequestFactory.setConnectTimeout(2000 * TIME_OUT_SECONDS);
        
        // 数据读取超时时间，即SocketTimeout
        clientHttpRequestFactory.setReadTimeout(50000);
        
        // 连接不够用的等待时间，不宜过长，必须设置，比如连接不够用时，时间过长将是灾难性的
        clientHttpRequestFactory.setConnectionRequestTimeout(200);
        
        // 缓冲请求数据，默认值是true。通过POST或者PUT大量发送数据时，建议将此属性更改为false，以免耗尽内存。
        clientHttpRequestFactory.setBufferRequestBody(false);
 
        // 添加内容转换器
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        messageConverters.add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
        messageConverters.add(new FormHttpMessageConverter());
//        messageConverters.add(new MappingJackson2XmlHttpMessageConverter());
        messageConverters.add(new MappingJackson2HttpMessageConverter());
 
        RestTemplate restTemplate = new RestTemplate(messageConverters);
        restTemplate.setRequestFactory(clientHttpRequestFactory);
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler());
        return restTemplate;
    }
 
    private RestClientFactory() {}
}
对于调用：
@Component
public class ClientImpl implements Client{
		
@Override
public DatabaseRet doPost(String url, Map<String, String> form_parameter) {
try {
	MultiValueMap<String, String> requestEntity = new LinkedMultiValueMap<>();
	form_parameter.keySet().stream().forEach(key -> requestEntity.add(key, MapUtils.getString(form_parameter, key, "")));

	ResponseEntity<DatabaseRet> response = RestClient.getClient().postForEntity(url, requestEntity, DatabaseRet.class);
    return response.getBody();
} catch (Exception e) {
    System.out.println("POST请求出错：{" + url + e.toString() + "}");
}

return null;
}
……
}

https://www.cnblogs.com/softidea/p/6690789.html

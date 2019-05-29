java后台解决跨域问题：
什么是CORS?
Cross-origin resource sharing(跨域资源共享),是一个W3C标准,它允许你向一个不同源的服务器发出XMLHttpRequest请求,从而克服了ajax只能请求同源服务的限制.并且也可以通过灵活的设置,来指定什么样的请求是可以被授权的.

什么是跨域?
假设你在http://xxx.com/test/下有一个js文件,从这个js里发出一个ajax请求请求后端服务,按照如下情况判定:

请求地址	原因	结果
http://xxx.com/xxxx/action	同一域名,不同文件夹	非跨域
http://xxx.com/test/action	同一域名,同一文件夹	非跨域
http://a.xxx.com/test/action	不同域名,文件路径相同	跨域
http://xxx.com:8080/test/action	同一域名,不同端口	跨域
https://xxx.com/test/action	同一域名,不同协议	跨域



解决跨域问题的方式有很多，这里主要是添加注解的方式和采用添加拦截器的方法：

方法一、spring boot中只用在Controller类上添加一个“@CrossOrigin“注解就可以实现对当前controller 的跨域 访问了，当然这个标签也可以加到方法上。

@CrossOrigin
public class CommonController {

}
其他controller类继承以上这个类就可以解决跨域问题。

注意：“@CrossOrigin“注解要求jdk1.8以上版本

方法二、采用添加拦截器的方法
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.pim.client.conf.SolrConfigProperties;

@SpringBootApplication
@MapperScan("com.pim.client.dao.mapper")
@ServletComponentScan
@EnableConfigurationProperties(SolrConfigProperties.class)
public class PIMClientApplication extends SpringBootServletInitializer implements WebMvcConfigurer//复写方法configureContentNegotiation，为解决url中格式和返回值格式不一致问题； addInterceptors为解决跨域问题，未测试是否有效{

	public static void main(String[] args) {
		SpringApplication.run(PIMClientApplication.class, args);
	}

	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		configurer.favorPathExtension(false);
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new HandlerInterceptor() {
			@Override
			public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
					throws Exception {

				response.addHeader("Access-Control-Allow-Origin", "*");
				response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
				response.addHeader("Access-Control-Allow-Headers",
						"Content-Type,X-Requested-With,accept,Origin,Access-Control-Request-Method,Access-Control-Request-Headers,token");
				return true;
			}

			@Override
			public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
					ModelAndView modelAndView) throws Exception {

			}

			@Override
			public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
					Exception ex) throws Exception {
			}
		});
	}
}


//以下是单独写了对WebMvcConfigurer的实现类，重写了addCorsMappings实现支持跨域，但是实际没有能生效
import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Configuration
@EnableWebMvc
public class WebConfiguration implements WebMvcConfigurer {
    
    /**
     * 修改json结构http/requestbody的转换器，
     * 在序列化的过程中过滤值为null的属性
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
            for (HttpMessageConverter<?> httpMessageConverter : converters) {
                if (httpMessageConverter instanceof MappingJackson2HttpMessageConverter) {
                    ((MappingJackson2HttpMessageConverter)httpMessageConverter).getObjectMapper()
                      .setDefaultPropertyInclusion(Include.NON_NULL);
                }
            }
    }
    
    /**
     * 全局跨域配置
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/pim/**")
        .allowedOrigins("*")
        .allowedHeaders("*")
        .allowedMethods("GET", "PUT")
        .allowCredentials(false);
    }
}


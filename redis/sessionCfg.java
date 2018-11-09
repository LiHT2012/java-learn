import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.session.data.redis.config.annotation.SpringSessionRedisConnectionFactory;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.HeaderHttpSessionIdResolver;
import org.springframework.session.web.http.HttpSessionIdResolver;

@Configuration
@EnableCaching
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 24*60*60*30)
public class SessionConfig {

    @Bean
    @SpringSessionRedisConnectionFactory//用于springsessionredis
    @Primary//用于有多个LettuceConnectionFactory定义时，告知redis应使用哪个作为首选
    @ConfigurationProperties(prefix="spring.redis")//从配置文件中获取相应配置信息
    //spring.redis.host-name    spring.redis.port
     public RedisConnectionFactory lettuceConnectionFactory() {
         RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();//其中是对hostName和port的set
         return new LettuceConnectionFactory(redisStandaloneConfiguration);
     }

    @Bean
    public HttpSessionIdResolver httpSessionIdResolver() {
        return HeaderHttpSessionIdResolver.xAuthToken();
    }

}

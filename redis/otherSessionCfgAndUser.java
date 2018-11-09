import java.time.Duration;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Configuration
public class WechatAccessTokenCacheManager {

    private static final String ACCESSTOKEN_REDIS_HOST = "118.31.60.143";
    private static final Integer ACCESSTOKEN_REDIS_HOST_PORT = 3333;

    @Bean(name="accessTokenCacheManager")
    public RedisCacheManager cacheManager(@Qualifier(value="accessTokenRedisConnectionFactory")RedisConnectionFactory connectionFactory) {
        RedisCacheManager redisCacheManager = RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig())
                .withInitialCacheConfigurations(Collections.singletonMap("wechat:accesstoken:", RedisCacheConfiguration
                        .defaultCacheConfig().prefixKeysWith("wechat:accesstoken:").entryTtl(Duration.ofMinutes(90))))
                .transactionAware().build();
        return redisCacheManager;
    }

    @Bean(name="accessTokenRedisConnectionFactory")
    public RedisConnectionFactory lettuceConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(ACCESSTOKEN_REDIS_HOST,
                ACCESSTOKEN_REDIS_HOST_PORT);
        redisStandaloneConfiguration.setDatabase(1);//选择redis数据库，默认为0
        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }
    /**
      用法：
      @Cacheable(value = "wechat:accesstoken:", key = "#tag", cacheManager = "accessTokenCacheManager")
	     public String test(String tag, String appId, String secretKey) {//所传tag即为key
    **/
}

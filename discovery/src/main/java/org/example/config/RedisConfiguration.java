package org.example.config;

import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * @Author: 边俊超
 * @Date: 2023/10/8 11:27
 */

@Configuration
public class RedisConfiguration {

    /**
     * spring-boot-autoconfigure的RedisAutoConfiguration自动注册的RedisTemplate，使用的序列化器为默人的JdkSerializationRedisSerializer，序列化后生成的是不利于阅读的编码字符串。
     * 所以我们手动注册一个RedisTemplate，设置RedisConnectionFactory属性为spring-boot-autoconfigure的JedisConnectionConfiguration/LettuceConnectionConfiguration自动注册的RedisConnectionFactory，并设置序列化器为StringRedisSerializer。
     * 其实也可以直接在主启动类中使用@Autowired注入SpringBoot自动注册的RedisTemplate，并添加一个@PostConstruct的方法来修改它的序列化器为StringRedisSerializer。
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        final RedisTemplate<String, Object> template = new RedisTemplate<>();
        // 设置ConnectionFactory，SpringBoot会自动注册ConnectionFactory-Bean
        template.setConnectionFactory(redisConnectionFactory);

        // 设置序列化器为StringRedisSerializer（默认是 JdkSerializationRedisSerializer , java操作时会产生乱码）
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);
        template.setHashValueSerializer(stringRedisSerializer);

        template.setValueSerializer(new JdkSerializationRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }

    /**
     * 配置StringRedisTemplate
     * @param factory
     * @return
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(
            RedisConnectionFactory factory) {
        StringRedisTemplate redisTemplate = new StringRedisTemplate();
        redisTemplate.setConnectionFactory(factory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        return redisTemplate;
    }

    /**
     * 支持使用Spring缓存注解操作Redis。（注意：主启动类上必须加@EnableCaching，开启支持缓存注解！）
     * 若使用了spring-boot-autoconfigure，只需在application.yml中配置spring.cacheh和spring.cache.redis即可，RedisCacheConfiguration会自动注册RedisCacheManager
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        // 初始化一个RedisCacheWriter
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory);

        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig();
        // 默认不过期
        defaultCacheConfig = defaultCacheConfig.entryTtl(Duration.ZERO)
                // 设置 key为string序列化
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                // 设置value为json序列化
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new Jackson2JsonRedisSerializer<>(
                                Object.class)));
        // 不缓存空值
        // .disableCachingNullValues();

        // 初始化RedisCacheManager
        return new RedisCacheManager(redisCacheWriter, defaultCacheConfig);
    }
}

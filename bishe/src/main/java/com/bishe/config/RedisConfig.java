package com.bishe.config;
 
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
 
/**
 * @author lzh
 * @date 2025/2/8 18:57
 */
@Configuration
public class RedisConfig {
 
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        //key 采用String的序列化的方式
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        //value的序列化采用jackson
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        //hash的 key也采用String序列化的方式
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        //hash的value也采用jackson
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        //注入连接工厂
        redisTemplate.setConnectionFactory(factory);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
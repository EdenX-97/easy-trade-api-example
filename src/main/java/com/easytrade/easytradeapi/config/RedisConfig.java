/*
 * @Description: Redis的配置类
 * @Author: Mo Xu
 * @Date: 2021-11-13 23:15:22
 * @LastEditors: Mo Xu
 * @LastEditTime: 2021-12-31 03:03:15
 * @FilePath: /EasyBuyCar/src/main/java/com/jiandanmaiche/api/config/RedisConfig.java
 */

package com.easytrade.easytradeapi.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.*;


@Configuration
@EnableCaching
public class RedisConfig {
    @Value("${spring.cache.expire}")
    private Integer expire;

    @Bean(name = "objectRedisTemplate")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        // 配置redisTemplate
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        // 设置序列化
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        // key序列化
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        // value序列化
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        // Hash key序列化
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        // Hash value序列化
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        RedisSerializer<String> redisSerializer = new StringRedisSerializer();
        //Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        // 解决查询缓存转换异常的问题
        //ObjectMapper om = new ObjectMapper();
        //om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        //om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
        //        ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        //jackson2JsonRedisSerializer.setObjectMapper(om);

        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
            ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer(om);

        // 配置序列化（解决乱码的问题）
        RedisCacheConfiguration config = RedisCacheConfiguration
                .defaultCacheConfig()
                .entryTtl(Duration.ofHours(expire)) // 默认的过期时间
                .computePrefixWith(name -> name + ":") // 单冒号
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer))
                .disableCachingNullValues();

        RedisCacheManager cacheManager = RedisCacheManager.builder(factory).cacheDefaults(config).build();
        return cacheManager;
    }

    //@Bean
    //public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
    //    RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();
    //    redisCacheConfiguration = redisCacheConfiguration
    //            // 设置key采用String的序列化方式
    //            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(StringRedisSerializer.UTF_8))
    //            //设置value序列化方式采用jackson方式序列化
    //            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer()))
    //            //当value为null时不进行缓存
    //            .disableCachingNullValues()
    //            //全局配置缓存过期时间【可以不配置】
    //            .entryTtl(Duration.ofHours(expire))
    //            .computePrefixWith(name -> name + ":");
    //
    //    return RedisCacheManager
    //            .builder(redisConnectionFactory)
    //            .cacheDefaults(redisCacheConfiguration)  //默认配置
    //            .build();
    //}
    //
    //@Bean
    //public StringRedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory) {
    //    GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer = serializer();
    //    StringRedisTemplate redisTemplate = new StringRedisTemplate();
    //    // key采用String的序列化方式
    //    redisTemplate.setKeySerializer(StringRedisSerializer.UTF_8);
    //    // value序列化方式采用jackson
    //    redisTemplate.setValueSerializer(genericJackson2JsonRedisSerializer);
    //    // hash的key也采用String的序列化方式
    //    redisTemplate.setHashKeySerializer(StringRedisSerializer.UTF_8);
    //    //hash的value序列化方式采用jackson
    //    redisTemplate.setHashValueSerializer(genericJackson2JsonRedisSerializer);
    //    redisTemplate.setConnectionFactory(redisConnectionFactory);
    //    return redisTemplate;
    //}
    //
    ///**
    // * 此方法不能用@Ben注解，避免替换Spring容器中的同类型对象
    // */
    //public GenericJackson2JsonRedisSerializer serializer() {
    //    return new GenericJackson2JsonRedisSerializer();
    //}

}

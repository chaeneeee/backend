package com.togedog.redis.tool;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class CacheConfig {

    @Value("${cache.redis.ttl:10}")  // TTL 값 yml 에 설정 (기본값: 10분)
    private long cacheTtl;
    private static final String CACHE_PREFIX = "togedogCache:"; // 캐시 키 Prefix 설정 (네임스페이스 충돌 방지 없어도 가능)
    @Bean(name = "customCacheManager")  //중복 캐시매니저 제거
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(cacheTtl))  // TTL 설정 (10분)
                .serializeKeysWith(RedisSerializationContext.SerializationPair.
                        fromSerializer(new StringRedisSerializer())) // 키 직렬화
                .serializeValuesWith(RedisSerializationContext.SerializationPair.
                        fromSerializer(new GenericJackson2JsonRedisSerializer())) //값 직렬화
                .prefixCacheNameWith(CACHE_PREFIX)  // 캐시 키 앞에 Prefix 추가
                .disableCachingNullValues(); // null 값 캐싱 방지

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(cacheConfig)
                .build();
    }
    }





package com.togedog.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheManager = "caffeineCacheManager") // JVM 캐시 (1차 캐시)
public class LocationService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final long TTL_SECONDS = 10000;

    // 자동 캐싱 + Redis 수동 저장
    @CachePut(value = "locationCache", key = "'location:' + #userEmail")
    public Location saveLocationCache(String userEmail, double latitude, double longitude) {
        System.out.println("📦 @CachePut - JVM 저장: " + userEmail);
        Location location = new Location(latitude, longitude);

        // Redis에도 수동 저장
        try {
            String redisKey = "location:" + userEmail;
            String redisValue = objectMapper.writeValueAsString(location);
            redisTemplate.opsForValue().set(redisKey, redisValue);
            redisTemplate.expire(redisKey, TTL_SECONDS, TimeUnit.SECONDS);
            System.out.println("Redis 수동 저장 완료: " + redisKey);
        } catch (JsonProcessingException e) {
            System.err.println("Redis 직렬화 오류: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Redis 저장 실패: " + e.getMessage());
        }

        return location;
    }

    // JVM 캐시 (1차 조회)
    @Cacheable(value = "locationCache", key = "'location:' + #userEmail")
    public Location getLocationCache(String userEmail) {
        System.out.println("getLocationCache() 메서드 진입: " + userEmail);
        System.out.println("@Cacheable - JVM 캐시 미스: " + userEmail);

        // Redis에서 직접 조회 시도
        try {
            String redisKey = "location:" + userEmail;
            String redisValue = (String) redisTemplate.opsForValue().get(redisKey);
            if (redisValue != null) {
                System.out.println("Redis 직접 조회 성공: " + redisKey);
                return objectMapper.readValue(redisValue, Location.class);
            }
        } catch (IOException e) {
            System.err.println("Redis 역직렬화 실패: " + e.getMessage());
        }

        return null;
    }

    @CacheEvict(value = "locationCache", key = "'location:' + #userEmail")
    public void deleteLocationCache(String userEmail) {
        System.out.println("🧹 JVM 캐시 삭제: " + userEmail);

        // Redis도 삭제
        try {
            String redisKey = "location:" + userEmail;
            redisTemplate.delete(redisKey);
            System.out.println("🧹 Redis 삭제 완료: " + redisKey);
        } catch (Exception e) {
            System.err.println("Redis 삭제 실패: " + e.getMessage());
        }
    }

    // 수동 캐싱 방식
//    public void saveLocationManual(String locationKey, double latitude, double longitude, String userEmail ) {
//        try {
//            String locationValue = objectMapper.writeValueAsString(new Location(latitude, longitude));
//            String finalLocationKey = locationKey + ":" + userEmail;
//            redisTemplate.opsForValue().set(finalLocationKey, locationValue);
//            redisTemplate.expire(finalLocationKey, TTL_SECONDS, TimeUnit.SECONDS);
//            System.out.println(" Redis 수동 저장 : " + finalLocationKey);
//        } catch (JsonProcessingException e) {
//            System.err.println("직렬화 실패: " + e.getMessage());
//        } catch (Exception e) {
//            System.err.println("Redis 저장 실패: " + e.getMessage());
//        }
//    }

//    public Location getLocationManual(String locationKey) {
//        try {
//            String locationValue = (String) redisTemplate.opsForValue().get(locationKey);
//            return objectMapper.readValue(locationValue, Location.class);
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    public Set<String> getKeysByPattern(String pattern) {
        return redisTemplate.keys(pattern);
    }

    @Getter
    @Setter
    public static class Location {
        private double latitude;
        private double longitude;

        public Location() {}

        public Location(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }
}

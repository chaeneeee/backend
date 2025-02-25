package com.togedog.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.togedog.exception.CacheOperationException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class LocationService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper(); // Jackson ObjectMapper를 사용하여 JSON 직렬화

    @Autowired
    @Qualifier("customCacheManager")  // ✅ 커스텀 캐시 매니저 사용 명확히 지정
    private CacheManager customCacheManager;

    private static final long TTL_SECONDS = 259200; // 3일 (259,200초)

    /**
     * 사용자 위치 정보를 Redis에 저장 (TTL 3일 적용)
     */
    public void saveLocation(String locationKey, double latitude, double longitude, String userEmail) {
        try {
            String locationValue = objectMapper.writeValueAsString(new Location(latitude, longitude, LocalDateTime.now()));
            String finalLocationKey = locationKey + ":" + userEmail;

            // Redis에 저장 (TTL 적용)
            redisTemplate.opsForValue().set(finalLocationKey, locationValue, TTL_SECONDS, TimeUnit.SECONDS);
            System.out.println("Location saved in Redis: " + finalLocationKey);
        } catch (JsonProcessingException e) {
            throw new CacheOperationException("데이터 직렬화 실패 (JSON 변환 오류)", e);
        } catch (RedisConnectionFailureException e) {
            throw new CacheOperationException("Redis 연결 실패 (서버 다운 또는 네트워크 오류)", e);
        } catch (DataAccessException e) {
            throw new CacheOperationException("Redis 데이터 접근 오류", e);
        } catch (Exception e) {
            throw new CacheOperationException("알 수 없는 Redis 저장 오류 발생", e);
        }
    }

    /**
     * 사용자 위치 정보를 Redis에서 조회
     * - 캐싱 적용 (Spring Cache 사용)
     */
    @Cacheable(value = "userLocationCache", key = "T(String).format('userLocation:%s:%s', #locationKey, #userEmail)", unless = "#result == null")
    public Location getLocation(String locationKey, String userEmail) {
        try {
            String finalKey = locationKey + ":" + userEmail;
            String locationValue = (String) redisTemplate.opsForValue().get(finalKey);

            if (locationValue == null) {
                System.out.println("⚠️ Redis에 저장된 위치 데이터 없음: " + finalKey);
                return null;
            }

            return objectMapper.readValue(locationValue, Location.class);
        } catch (IOException e) {
            throw new CacheOperationException("데이터 역직렬화 실패 (JSON 변환 오류)", e);
        } catch (RedisConnectionFailureException e) {
            throw new CacheOperationException("Redis 연결 실패 (서버 다운 또는 네트워크 오류)", e);
        } catch (DataAccessException e) {
            throw new CacheOperationException("Redis 데이터 접근 오류", e);
        } catch (Exception e) {
            throw new CacheOperationException("알 수 없는 Redis 조회 오류 발생", e);
        }
    }

    /**
     * 특정 패턴을 가진 Redis 키 목록 조회
     */
    public Set<String> getKeysByPattern(String pattern) {
        try {
            return redisTemplate.keys(pattern);
        } catch (RedisConnectionFailureException e) {
            throw new CacheOperationException("Redis 연결 실패 (서버 다운 또는 네트워크 오류)", e);
        } catch (DataAccessException e) {
            throw new CacheOperationException("Redis 데이터 접근 오류", e);
        } catch (Exception e) {
            throw new CacheOperationException("알 수 없는 Redis 키 조회 오류 발생", e);
        }
    }

    /**
     * 특정 사용자 위치 데이터 삭제 (캐시 무효화)
     */
    @CacheEvict(value = "userLocationCache", key = "#locationKey + ':' + #userEmail")
    public void deleteLocation(String locationKey, String userEmail) {
        try {
            String finalKey = locationKey + ":" + userEmail;
            redisTemplate.delete(finalKey);
            System.out.println("Location deleted from Redis: " + finalKey);
        } catch (Exception e) {
            throw new CacheOperationException("Redis 삭제 오류 발생", e);
        }
    }

    @Getter
    @Setter
    public static class Location {
        private double latitude;
        private double longitude;
        private LocalDateTime storedAt;

        public Location() {}

        public Location(double latitude, double longitude, LocalDateTime storedAt) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.storedAt = storedAt;
        }
    }
}

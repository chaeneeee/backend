package com.togedog.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.persistence.Cacheable;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.TimeUnit;
@Service
@RequiredArgsConstructor
public class MarkerService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final long TTL_SECONDS = 259200;  // 3일

    public Marker getMarker(String markerKey) {
        try {
            // Redis에서 값 가져오기
            String markerValue = (String) redisTemplate.opsForValue().get(markerKey);
            if (markerValue == null) {
                return null;
            }
            // JSON을 Marker 객체로 변환
            Marker marker = objectMapper.readValue(markerValue, Marker.class);
            marker.setEmail(markerKey); // 마커 키 정보 설정
            return marker;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Set<String> getKeysByPattern(String pattern) {
        return redisTemplate.keys(pattern);
    }
//사용자의 마커 데이터를 저장 (기존 데이터 삭제 후 갱신)
public void saveMarker(String markerKey, double latitude, double longitude, String userEmail) {
    try {
        Marker marker = new Marker(userEmail, latitude, longitude);
        String markerValue = objectMapper.writeValueAsString(marker);

        deleteMarker(markerKey); // 기존 데이터 삭제
        redisTemplate.opsForValue().set(markerKey, markerValue);
        redisTemplate.expire(markerKey, TTL_SECONDS, TimeUnit.SECONDS);
    } catch (JsonProcessingException e) {
        e.printStackTrace();
    }
}

    public void deleteMarker(String markerKey) {
        if (Boolean.TRUE.equals(redisTemplate.hasKey(markerKey))) {
            redisTemplate.delete(markerKey);
        }
    }


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Marker {
        private String email;
        private double latitude;
        private double longitude;
    }
}

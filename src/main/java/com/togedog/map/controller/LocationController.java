package com.togedog.map.controller;

import com.togedog.redis.LocationService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.security.core.Authentication;

@RestController
@CrossOrigin(origins = "http://localhost:3000")  // localhost:3000에서 오는 요청을 허용
public class LocationController {

    @Value("${kakao.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final LocationService locationService;

    public LocationController(RestTemplate restTemplate, LocationService locationService) {
        this.restTemplate = restTemplate;
        this.locationService = locationService;
    }

    // 현재 위치를 처리하고 Kakao API 호출
    @PostMapping("/currentLocation")
    public ResponseEntity<Object> currentLocation(@RequestBody LocationRequest locationRequest, Authentication authentication) {
        double latitude = locationRequest.getLatitude();
        double longitude = locationRequest.getLongitude();
        String userEmail = authentication.getName();

        // 위도와 경도를 Redis에 저장
        locationService.saveLocation("user:location", latitude, longitude, userEmail);

        // Kakao API 호출
        UriComponents uriComponents = UriComponentsBuilder
                .newInstance()
                .scheme("https")
                .host("dapi.kakao.com")
                .path("/v2/local/geo/coord2address.json")
                .queryParam("x", longitude)
                .queryParam("y", latitude)
                .encode()
                .build();
        String url = uriComponents.toUriString();

        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + apiKey);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Kakao API 요청
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        // Kakao API 응답을 그대로 프론트엔드로 반환
        return ResponseEntity.ok(response.getBody());
    }

    // 저장된 위치 데이터를 반환
    @GetMapping("/location")
    public ResponseEntity<LocationService.Location> getLocation(Authentication authentication) {
        String userEmail = authentication.getName(); // 현재 로그인한 사용자 이메일 가져오기
        LocationService.Location location = locationService.getLocation("user:location", userEmail);
        return ResponseEntity.ok(location);
    }


    @Getter
    @Setter
    public static class LocationRequest {
        private double latitude;
        private double longitude;
        private String userEmail;
    }
}

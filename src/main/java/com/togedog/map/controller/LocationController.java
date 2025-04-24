package com.togedog.map.controller;

import com.togedog.redis.LocationService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api")
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


    @PostMapping("/currentLocation")
    public ResponseEntity<Object> currentLocation(@RequestBody LocationRequest request, Authentication authentication) {
        String userEmail = authentication.getName();
        double latitude = request.getLatitude();
        double longitude = request.getLongitude();

        // 자동 캐싱 방식 저장
        locationService.saveLocationCache(userEmail, latitude, longitude);

        // Kakao API 호출
        String url = UriComponentsBuilder
                .fromHttpUrl("https://dapi.kakao.com/v2/local/geo/coord2address.json")
                .queryParam("x", longitude)
                .queryParam("y", latitude)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + apiKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        return ResponseEntity.ok(response.getBody());
    }

    //자동 캐싱으로 저장된 위치 조회
    @GetMapping("/location")
    public ResponseEntity<LocationService.Location> getLocation(Authentication authentication) {
        String userEmail = authentication.getName();
        LocationService.Location location = locationService.getLocationCache(userEmail);
        return ResponseEntity.ok(location);
    }

   //자동캐시 삭제
    @DeleteMapping("/location")
    public ResponseEntity<?> deleteLocation(Authentication authentication) {
        String userEmail = authentication.getName();
        locationService.deleteLocationCache(userEmail);
        return ResponseEntity.ok().build();
    }

//
//   //수동캐시 위치 저장
//    @PostMapping("/currentLocation/manual")
//    public ResponseEntity<?> currentLocationManual(@RequestBody LocationRequest request, Authentication authentication) {
//        String userEmail = authentication.getName();
//        locationService.saveLocation("user:location", request.getLatitude(), request.getLongitude(), userEmail);
//        return ResponseEntity.ok().build();
//    }
//
//    //수동 캐시 위치 조회
//    @GetMapping("/location/manual")
//    public ResponseEntity<LocationService.Location> getLocationManual(Authentication authentication) {
//        String userEmail = authentication.getName();
//        LocationService.Location location = locationService.getLocationManual("user:location:" + userEmail);
//        return ResponseEntity.ok(location);
//    }


    @Getter
    @Setter
    public static class LocationRequest {
        private double latitude;
        private double longitude;
        private String userEmail;
    }
}

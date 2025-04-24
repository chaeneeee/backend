package com.togedog.map.config;


import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

    @Configuration
    public class AppConfig {

        @Bean
        public RestTemplate restTemplate() {
            CloseableHttpClient httpClient = HttpClients.custom()
                    .setMaxConnTotal(100) // 전체 최대 연결 수 설정
                    .setMaxConnPerRoute(20) // 각 라우트당 최대 연결 수 설정
                    .build(); // HttpClient 객체 생성


            HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);

            //  RestTemplate 생성
            return new RestTemplate(factory); // 커스터마이징된 HttpClient를 사용하는 RestTemplate 객체 반환
        }
    }



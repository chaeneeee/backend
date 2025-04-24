package com.togedog.scheduler;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling  // 스케줄링 활성화
@EnableAsync       // 비동기 처리 활성화
public class SchedulerConfig {
    // 추가 설정이 필요 없다면 이 클래스는 비어 있을 수 있습니다.
}
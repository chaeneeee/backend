package com.togedog.scheduler;

import com.togedog.matchingStandBy.service.MatchingStandByService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class TimeCheckScheduler {
    private final MatchingStandByService service;

    // 비동기 스케줄러: 1분마다 현재 시간 출력
    @Async
    @Scheduled(fixedDelay = 60000)  // 1분마다 실행 (60,000 밀리초 = 1분)
    public void checkTime() {
        // 현재 시간을 출력하는 로직
        LocalDateTime now = LocalDateTime.now();
//        service.changeStatusToTimeOut(now);


//        System.out.println("현재 시간: " + formattedNow + " - 스레드: " + Thread.currentThread().getName());
    }
}

package com.lemontree.interview.scheduler;

import com.lemontree.interview.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 결제 한도 초기화 스케줄러입니다.
 *
 * @author 정승조
 * @version 2024. 08. 08.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LimitResetScheduler {

    private final MemberService memberService;

    /**
     * 매일 00시에 일일 한도를 초기화합니다.
     */
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void resetDailyLimit() {
        try {
            memberService.resetDailyLimit();
        } catch (Exception e) {
            log.error("일일 한도 초기화에 실패하였습니다. [{}]", e.getMessage());
        }
    }

    /**
     * 매월 1일 00시에 월간 한도를 초기화합니다.
     */
    @Scheduled(cron = "0 0 0 1 * *", zone = "Asia/Seoul")
    public void resetMonthlyLimit() {
        try {
            memberService.resetMonthlyLimit();
        } catch (Exception e) {
            log.error("월간 한도 초기화에 실패하였습니다. [{}]", e.getMessage());
        }
    }

    /**
     * 30초 마다 한도를 초기화합니다.
     */
    @Scheduled(cron = "*/30 * * * * *", zone = "Asia/Seoul")
    public void resetLimit() {

        log.info("1분 스케줄러 실행");

        try {
            memberService.resetDailyLimit();
        } catch (Exception e) {
            log.error("한도 초기화에 실패하였습니다.", e);
        }
    }
}

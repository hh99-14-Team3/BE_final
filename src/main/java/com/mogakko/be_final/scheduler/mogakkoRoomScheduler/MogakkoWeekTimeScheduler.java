package com.mogakko.be_final.scheduler.mogakkoRoomScheduler;

import com.mogakko.be_final.domain.members.entity.MemberWeekStatistics;
import com.mogakko.be_final.domain.members.repository.MemberWeekStatisticsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 스케줄러를 사용하여, 매주 월요일 오전 5시 유저의 주간 통계 기본값으로 update
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class MogakkoWeekTimeScheduler {
    private final MemberWeekStatisticsRepository memberWeekStatisticsRepository;

    @Async
    @Scheduled(cron = "0 0 5 * * 1")
    @Transactional
    public void updateWeekTimeOfMembers() {
        log.info("===== updateWeekTimeOfMembers Scheduler 호출");
        List<MemberWeekStatistics> memberWeekStatistics = memberWeekStatisticsRepository.findAll();

        for (MemberWeekStatistics mbws : memberWeekStatistics) {
            mbws.init();
        }
    }
}

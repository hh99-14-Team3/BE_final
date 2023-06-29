package com.mogakko.be_final.scheduler.mogakkoRoomScheduler;

import com.mogakko.be_final.domain.members.entity.MemberWeekStatistics;
import com.mogakko.be_final.domain.members.repository.MemberWeekStatisticsRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
class MogakkoWeekTimeSchedulerTest {

    @Mock
    MemberWeekStatisticsRepository memberWeekStatisticsRepository;
    @InjectMocks
    MogakkoWeekTimeScheduler mogakkoWeekTimeScheduler;

    @DisplayName("updateWeekTimeOfMembers Method 성공 테스트")
    @Test
    void updateWeekTimeOfMembers() {
        // given
        MemberWeekStatistics memberWeekStatistics = MemberWeekStatistics.builder()
                .email("test@test.com")
                .thu(10).wed(20).fri(30).sat(40).tue(50).mon(60).sun(70).weekTotalTime(280)
                .build();

        List<MemberWeekStatistics> memberWeekStatisticsList = new ArrayList<>();
        memberWeekStatisticsList.add(memberWeekStatistics);

        when(memberWeekStatisticsRepository.findAll()).thenReturn(memberWeekStatisticsList);

        // when
        mogakkoWeekTimeScheduler.updateWeekTimeOfMembers();

        // then
        assertEquals(0, memberWeekStatistics.getFri());
        assertEquals(0, memberWeekStatistics.getThu());
        assertEquals(0, memberWeekStatistics.getSun());
        assertEquals(0, memberWeekStatistics.getMon());
        assertEquals(0, memberWeekStatistics.getTue());
        assertEquals(0, memberWeekStatistics.getSat());
        assertEquals(0, memberWeekStatistics.getWed());
        assertEquals(0, memberWeekStatistics.getWeekTotalTime());
    }
}
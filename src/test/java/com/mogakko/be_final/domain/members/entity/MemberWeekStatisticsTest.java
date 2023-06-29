package com.mogakko.be_final.domain.members.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MemberWeekStatisticsTest {

    @DisplayName("NoArgsConstructor 테스트")
    @Test
    void noArgsConstructor() {
        MemberWeekStatistics memberWeekStatistics = new MemberWeekStatistics();

        assertNotNull(memberWeekStatistics);
    }

}
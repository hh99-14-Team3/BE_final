package com.mogakko.be_final.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TimeUtilTest {

    @Nested
    @DisplayName("TImeUtil class 테스트")
    class Time {
        @DisplayName("TimeUtil 테스트")
        @Test
        void timeUtil_test() {
            TimeUtil timeUtil = new TimeUtil();
        }
    }
}
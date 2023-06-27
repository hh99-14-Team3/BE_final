package com.mogakko.be_final.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BadWordFilteringTest {

    @InjectMocks
    BadWordFiltering badWordFiltering;

    @Nested
    @DisplayName("checkBadWord Method 테스트")
    class CheckBadWord {
        @DisplayName("욕설 필터링 테스트")
        @Test
        void checkBadWord_filteringBadWord() {
            // given
            String str = "시3232발.";

            // when
            String filteringStr = badWordFiltering.checkBadWord(str);

            // then
            assertEquals("******.", filteringStr);
        }

        @DisplayName("욕설 필터링 테스트 2")
        @Test
        void checkBadWord_notBadWord() {
            // given
            String str = "안녕하셈.";

            // when
            String filteringStr = badWordFiltering.checkBadWord(str);

            // then
            assertEquals(str, filteringStr);
        }
    }

    @Test
    void checkBadWordUUID() {
    }
}
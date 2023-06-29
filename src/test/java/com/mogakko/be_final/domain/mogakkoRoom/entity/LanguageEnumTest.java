package com.mogakko.be_final.domain.mogakkoRoom.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LanguageEnumTest {


    @Nested
    @DisplayName("from Method 테스트")
    class From {
        @DisplayName("from 성공 테스트")
        @Test
        void from() {
            // given
            String value = "JAVA";

            // when
            LanguageEnum languageEnum = LanguageEnum.from(value);

            // then
            assertEquals(LanguageEnum.JAVA, languageEnum);
        }
    }

    @Test
    void getValue() {
    }
}
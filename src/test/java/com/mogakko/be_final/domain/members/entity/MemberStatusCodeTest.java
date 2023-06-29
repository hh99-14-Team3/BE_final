package com.mogakko.be_final.domain.members.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith({MockitoExtension.class})
class MemberStatusCodeTest {

    @Nested
    @DisplayName("from Method 테스트")
    class From {
        @DisplayName("from Method 성공 테스트")
        @Test
        void from_success() {
            // given
            String value = "200";

            // when
            MemberStatusCode memberStatusCode = MemberStatusCode.from(value);

            // then
            assertEquals(MemberStatusCode.NORMAL, memberStatusCode);
        }

        @DisplayName("from Method 실패 테스트")
        @Test
        void from_failWithReturnNull() {
            // given
            String value = "2222222";

            // when
            MemberStatusCode memberStatusCode = MemberStatusCode.from(value);

            // then
            assertNull(memberStatusCode);
        }
    }
}
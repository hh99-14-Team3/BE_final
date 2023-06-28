package com.mogakko.be_final.domain.mogakkoRoom.dto.response;

import com.mogakko.be_final.domain.mogakkoRoom.entity.LanguageEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("MogakkoRoomCreateResponseDto 테스트")
@ExtendWith(MockitoExtension.class)
class MogakkoRoomCreateResponseDtoTest {
    MogakkoRoomCreateResponseDto mogakkoRoomCreateResponseDto = MogakkoRoomCreateResponseDto.builder()
            .sessionId("sessionId")
            .title("내가코딩왕")
            .isOpened(true)
            .language(LanguageEnum.JAVA)
            .password("")
            .build();

    @DisplayName("MogakkoRoomCreateResponseDto Getter 테스트")
    @Nested
    class testGetter {
        @DisplayName("getSessionId 테스트")
        @Test
        void getSessionId() {
            String sessionId = mogakkoRoomCreateResponseDto.getSessionId();
            assertEquals("sessionId", sessionId);
        }

        @DisplayName("getTitle 테스트")
        @Test
        void getTitle() {
            String title = mogakkoRoomCreateResponseDto.getTitle();
            assertEquals("내가코딩왕", title);
        }

        @DisplayName("isOpened 테스트")
        @Test
        void isOpened() {
            boolean isOpened = mogakkoRoomCreateResponseDto.isOpened();
            assertTrue(isOpened);
        }

        @DisplayName("getLanguage 테스트")
        @Test
        void getLanguage() {
            LanguageEnum language = mogakkoRoomCreateResponseDto.getLanguage();
            assertEquals(LanguageEnum.JAVA, language);
        }

        @DisplayName("getPassword 테스트")
        @Test
        void getPassword() {
            String password = mogakkoRoomCreateResponseDto.getPassword();
            assertEquals("", password);
        }

        @DisplayName("getCreatedAt 테스트")
        @Test
        void getCreatedAt() {
            LocalDateTime createdAt = mogakkoRoomCreateResponseDto.getCreatedAt();
            assertEquals(null, createdAt);
        }

        @DisplayName("getModifiedAt 테스트")
        @Test
        void getModifiedAt() {
            LocalDateTime modifiedAt = mogakkoRoomCreateResponseDto.getModifiedAt();
            assertEquals(null, modifiedAt);
        }
    }
}
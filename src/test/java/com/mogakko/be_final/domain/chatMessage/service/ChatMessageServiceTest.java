package com.mogakko.be_final.domain.chatMessage.service;

import com.mogakko.be_final.domain.chatMessage.dto.ChatMessage;
import com.mogakko.be_final.exception.CustomException;
import com.mogakko.be_final.util.BadWordFiltering;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import static com.mogakko.be_final.exception.ErrorCode.PLZ_INPUT_CONTENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChatMessageServiceTest {

    @Mock
    ChannelTopic channelTopic;
    @Mock
    RedisTemplate redisTemplate;
    @Mock
    BadWordFiltering badWordFiltering;
    @InjectMocks
    ChatMessageService chatMessageService;

    @Nested
    @DisplayName("채팅 테스트")
    class SendChatMessage {
        @DisplayName("채팅 보내기 성공 테스트")
        @Test
        void sendChatMessage_success() {
            // given
            ChatMessage chatMessage = ChatMessage.builder()
                    .type(ChatMessage.MessageType.TALK)
                    .sessionId("sessionId")
                    .nickname("nickname")
                    .message("msg")
                    .build();
            when(badWordFiltering.checkBadWord(chatMessage.getMessage())).thenReturn(chatMessage.getMessage());

            // when
            chatMessageService.sendChatMessage(chatMessage);

            // then
            verify(badWordFiltering, times(1)).checkBadWord(chatMessage.getMessage());
            verify(redisTemplate, times(1)).convertAndSend(channelTopic.getTopic(), chatMessage);
        }

        @DisplayName("채팅 타입 ENTER 성공 테스트")
        @Test
        void sendChatMessage_successWithTypeEnter() {
            // given
            ChatMessage chatMessage = ChatMessage.builder()
                    .type(ChatMessage.MessageType.ENTER)
                    .sessionId("sessionId")
                    .nickname("nickname")
                    .message("msg")
                    .build();
            when(badWordFiltering.checkBadWord(chatMessage.getMessage())).thenReturn(chatMessage.getMessage());

            // when
            chatMessageService.sendChatMessage(chatMessage);

            // then
            verify(badWordFiltering, times(1)).checkBadWord(chatMessage.getMessage());
        }

        @DisplayName("빈 문자열 채팅 보내기 예외 테스트")
        @Test
        void sendChatMessage_noInputMessage() {
            // given
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setMessage("");
            chatMessage.setNickname("John");
            chatMessage.setType(ChatMessage.MessageType.ENTER);
            chatMessage.setSessionId("123");

            when(badWordFiltering.checkBadWord(chatMessage.getMessage())).thenReturn("");

            // when & then
            CustomException customException = assertThrows(CustomException.class, () -> chatMessageService.sendChatMessage(chatMessage));
            assertEquals(PLZ_INPUT_CONTENT, customException.getErrorCode());
            verify(badWordFiltering, times(1)).checkBadWord(chatMessage.getMessage());
            verify(redisTemplate, never()).convertAndSend(channelTopic.getTopic(), chatMessage);
        }
    }
}

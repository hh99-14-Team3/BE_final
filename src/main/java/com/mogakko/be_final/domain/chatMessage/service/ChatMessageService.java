package com.mogakko.be_final.domain.chatMessage.service;

import com.mogakko.be_final.domain.chatMessage.dto.ChatMessage;
import com.mogakko.be_final.exception.CustomException;
import com.mogakko.be_final.redis.util.RedisUtil;
import com.mogakko.be_final.util.BadWordFiltering;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.mogakko.be_final.exception.ErrorCode.PLZ_INPUT_CONTENT;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final ChannelTopic channelTopic;
    private final RedisTemplate redisTemplate;
    private final BadWordFiltering badWordFiltering;

    // 메세지 발송
    public void sendChatMessage(ChatMessage chatMessage) {
        log.info("sendChatMessage 확인");
        String filteredMessage = badWordFiltering.checkBadWord(chatMessage.getMessage());
        if (filteredMessage.equals("")) {
            throw new CustomException(PLZ_INPUT_CONTENT);
        }
        chatMessage.setMessage(filteredMessage);

        String msg = chatMessage.getNickname() + "님이 입장했습니다.";
        if (ChatMessage.MessageType.ENTER.equals(chatMessage.getType())) {
            chatMessage = ChatMessage.builder()
                    .type(ChatMessage.MessageType.ENTER)
                    .sessionId(chatMessage.getSessionId())
                    .nickname(chatMessage.getNickname())
                    .message(msg)
                    .build();
        }
        redisTemplate.convertAndSend(channelTopic.getTopic(), chatMessage);
    }
}

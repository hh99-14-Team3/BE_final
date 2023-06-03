package com.mogakko.be_final.domain.chatMessage.service;

import com.mogakko.be_final.domain.chatMessage.dto.ChatMessage;
import com.mogakko.be_final.redis.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final ChannelTopic channelTopic;
    private final RedisUtil redisUtil;
    private final RedisTemplate redisTemplate;

    // 메세지 발송
    public void sendChatMessage(ChatMessage chatMessage) {
        log.info("sendChatMessage 확인");
        if (ChatMessage.MessageType.ENTER.equals(chatMessage.getType())) {
            ChatMessage.builder()
                    .type(ChatMessage.MessageType.ENTER)
                    .sessionId(chatMessage.getSessionId())
                    .nickname(chatMessage.getNickname())
                    .message(chatMessage.getNickname() + "님이 입장했습니다.");
        }
        redisTemplate.convertAndSend(channelTopic.getTopic(), chatMessage);
        String messageData = chatMessage.getType() + "/" + chatMessage.getSessionId() + "/" + chatMessage.getNickname() + "/" + chatMessage.getMessage();
        redisUtil.set(UUID.randomUUID().toString(), messageData, 60 * 24 * 1000 * 10);
    }
}

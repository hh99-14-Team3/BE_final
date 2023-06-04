package com.mogakko.be_final.redis.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mogakko.be_final.domain.chatMessage.dto.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisSubscriber {

    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate messagingTemplate;

    public void sendMessage(String publishMessage) {
        try {
            // ChatMessage 객채로 맵핑
            ChatMessage chatMessage = objectMapper.readValue(publishMessage, ChatMessage.class);
            // 채팅방을 구독한 클라이언트에게 메시지 발송
            log.info("redis subscriber 확인");
            messagingTemplate.convertAndSend("/sub/chat/room/" + chatMessage.getSessionId(), chatMessage);
        } catch (Exception e) {
            log.error("Exception {}", e);
        }
    }
}
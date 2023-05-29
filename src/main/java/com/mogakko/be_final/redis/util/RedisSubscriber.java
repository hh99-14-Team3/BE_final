package com.mogakko.be_final.redis.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mogakko.be_final.domain.chatMessage.dto.ChatMessageResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final RedisTemplate redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Redis에서 메시지가 발행(publish)되면 대기하고 있던 Redis Subscriber가 해당 메시지를 받아 처리한다.
     */

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            log.info("onMessage");

            String publishMessage = (String) redisTemplate.getStringSerializer().deserialize(message.getBody());

            log.info("publishMessage = " + publishMessage);
            ChatMessageResponseDto chatMessageResponseDto = objectMapper.readValue(publishMessage, ChatMessageResponseDto.class);

            messagingTemplate.convertAndSend("/sub/chat/room/" + chatMessageResponseDto.getSessionId(), chatMessageResponseDto);

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}

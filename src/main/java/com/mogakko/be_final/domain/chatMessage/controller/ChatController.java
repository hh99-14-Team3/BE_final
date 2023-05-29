package com.mogakko.be_final.domain.chatMessage.controller;

import com.mogakko.be_final.domain.chatMessage.dto.ChatMessageRequestDto;
import com.mogakko.be_final.domain.chatMessage.dto.ChatMessageResponseDto;
import com.mogakko.be_final.domain.chatMessage.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatMessageService chatMessageService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic channelTopic;

    @ResponseBody
    @MessageMapping("/chat/room")
    public void message(ChatMessageRequestDto chatMessageRequestDto) {
        ChatMessageResponseDto chatMessageResponseDto = chatMessageService.ChatMessageCreate(chatMessageRequestDto);
        redisTemplate.convertAndSend(channelTopic.getTopic(), chatMessageResponseDto);
    }
}

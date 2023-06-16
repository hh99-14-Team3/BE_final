package com.mogakko.be_final.domain.chatMessage.service;

import com.mogakko.be_final.domain.chatMessage.dto.ChatMessage;
import com.mogakko.be_final.util.BadWordFiltering;
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
    private final BadWordFiltering badWordFiltering;

    // 메세지 발송
    public void sendChatMessage(ChatMessage chatMessage) {
        log.info("sendChatMessage 확인");
        String filteredMessage = badWordFiltering.checkBadWordString(chatMessage.getMessage());
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
        //TODO : 이거 꼭 저장해야 할까요? 저장하면 쓸 일이 있을까 해서 적어놓긴 했는데 딱히 쓸만한 곳이 없네요 쩝..
        String messageData = chatMessage.getType() + "/" + chatMessage.getSessionId() + "/" + chatMessage.getNickname() + "/" + chatMessage.getMessage();
        redisUtil.set(UUID.randomUUID().toString(), messageData, 60 * 24 * 1000 * 10);
    }
}

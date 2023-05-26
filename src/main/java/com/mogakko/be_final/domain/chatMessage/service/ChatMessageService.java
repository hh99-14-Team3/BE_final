package com.mogakko.be_final.domain.chatMessage.service;

import com.mogakko.be_final.domain.chatMessage.dto.ChatMessageRequestDto;
import com.mogakko.be_final.domain.chatMessage.dto.ChatMessageResponseDto;
import com.mogakko.be_final.domain.chatMessage.entity.RoomMessage;
import com.mogakko.be_final.domain.chatMessage.repository.RoomMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ChatMessageService {
    private final RoomMessageRepository roomMessageRepository;

    public ChatMessageResponseDto ChatMessageCreate(ChatMessageRequestDto chatMessageRequestDto) {
        ChatMessageResponseDto chatMessageResponseDto;
        RoomMessage roomMessage = new RoomMessage(chatMessageRequestDto);
        roomMessageRepository.save(roomMessage);
        chatMessageResponseDto = new ChatMessageResponseDto(roomMessage);
        return chatMessageResponseDto;
    }


}

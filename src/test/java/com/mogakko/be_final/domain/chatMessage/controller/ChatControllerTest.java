package com.mogakko.be_final.domain.chatMessage.controller;

import com.mogakko.be_final.domain.chatMessage.dto.ChatMessage;
import com.mogakko.be_final.domain.chatMessage.service.ChatMessageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DisplayName("ChatControllerTest 테스트")
@ExtendWith(MockitoExtension.class)
class ChatControllerTest {
    @Mock
    ChatMessageService chatMessageService;
    @InjectMocks
    ChatController chatController;

    @DisplayName("ChatControllerTest 테스트")
    @Test
    void message() {
        // Given
        ChatMessage chatMessage = new ChatMessage();
        // When
        chatController.message(chatMessage);
        // Then
        verify(chatMessageService).sendChatMessage(chatMessage);
    }
}
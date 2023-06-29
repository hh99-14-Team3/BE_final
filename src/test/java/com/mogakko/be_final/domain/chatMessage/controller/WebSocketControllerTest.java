package com.mogakko.be_final.domain.chatMessage.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("WebSocketController 테스트")
@ExtendWith(MockitoExtension.class)
class WebSocketControllerTest {

    @DisplayName("WebSocketController 테스트")
    @Test
    void sendMessage() {
        // Given
        WebSocketController webSocketController = new WebSocketController();
        String message = "Test message";

        // When
        String result = webSocketController.sendMessage(message);

        // Then
        assertEquals(message, result);
    }
}
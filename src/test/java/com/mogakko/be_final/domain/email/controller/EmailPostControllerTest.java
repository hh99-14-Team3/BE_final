package com.mogakko.be_final.domain.email.controller;

import com.mogakko.be_final.domain.email.dto.request.EmailConfirmRequestDto;
import com.mogakko.be_final.domain.email.service.EmailPostService;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.mogakkoRoom.controller.MogakkoPostController;
import com.mogakko.be_final.domain.mogakkoRoom.dto.request.MogakkoRoomCreateRequestDto;
import com.mogakko.be_final.domain.mogakkoRoom.dto.request.MogakkoRoomEnterDataRequestDto;
import com.mogakko.be_final.domain.mogakkoRoom.service.MogakkoPostService;
import com.mogakko.be_final.util.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Email Controller - [POST] 테스트")
class EmailPostControllerTest {
    @Mock
    private EmailPostService emailPostService;
    @Mock
    private EmailConfirmRequestDto emailConfirmRequestDto;
    @InjectMocks
    private EmailPostController emailPostController;
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(emailPostController).build();
    }

    @DisplayName("[POST] 이메일 전송 테스트")
    @Test
    void sendEmailToFindPassword() throws Exception{
        Message message = new Message("이메일을 성공적으로 보냈습니다.", null);
        when(emailPostService.sendSimpleMessage(any(EmailConfirmRequestDto.class))).thenReturn(ResponseEntity.ok(message));

        ResponseEntity<Message> response = emailPostController.sendEmailToFindPassword(emailConfirmRequestDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(message, response.getBody());
    }
}
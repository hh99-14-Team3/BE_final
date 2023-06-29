package com.mogakko.be_final.domain.sse.controller;

import com.mogakko.be_final.domain.members.entity.MemberStatusCode;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.entity.Role;
import com.mogakko.be_final.domain.sse.service.NotificationSearchService;
import com.mogakko.be_final.domain.sse.service.NotificationService;
import com.mogakko.be_final.userDetails.UserDetailsImpl;
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
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DisplayName("Notification Controller - [GET] 테스트")
@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {
    @Mock
    NotificationService notificationService;
    @Mock
    NotificationSearchService notificationSearchService;
    @InjectMocks
    NotificationController notificationController;


    Members member = Members.builder()
            .id(1L)
            .email("test@example.com")
            .nickname("nickname")
            .password("password1!")
            .role(Role.USER)
            .codingTem(36.5)
            .mogakkoTotalTime(0L)
            .memberStatusCode(MemberStatusCode.BASIC)
            .profileImage("https://source.boringavatars.com/beam/120/$" + "nickname" + "?colors=00F0FF,172435,394254,EAEBED,F9F9FA")
            .friendCode(123456)
            .isTutorialCheck(false)
            .build();

    UserDetailsImpl userDetails = new UserDetailsImpl(member, member.getEmail());

    @DisplayName("[GET] SSE 구독 테스트")
    @Test
    void subscribe() {
        // Given
        String lastEventId = "lastEventId";
        SseEmitter expectedEmitter = new SseEmitter();
        when(notificationService.subscribe(anyLong(), anyString())).thenReturn(expectedEmitter);
        // When
        SseEmitter result = notificationController.subscribe(userDetails, lastEventId);
        // Then
        assertEquals(expectedEmitter, result);
        verify(notificationService, times(1)).subscribe(anyLong(), anyString());
    }
    
}

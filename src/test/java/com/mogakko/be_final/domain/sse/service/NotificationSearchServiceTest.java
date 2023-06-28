package com.mogakko.be_final.domain.sse.service;

import com.mogakko.be_final.domain.members.entity.MemberStatusCode;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.entity.Role;
import com.mogakko.be_final.domain.members.entity.SocialType;
import com.mogakko.be_final.domain.sse.dto.response.NotificationResponseDto;
import com.mogakko.be_final.domain.sse.entity.Notification;
import com.mogakko.be_final.domain.sse.entity.NotificationType;
import com.mogakko.be_final.domain.sse.repository.NotificationRepository;
import com.mogakko.be_final.util.Message;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
class NotificationSearchServiceTest {

    @Mock
    NotificationRepository notificationRepository;
    @InjectMocks
    NotificationSearchService notificationSearchService;

    Members receiver = Members.builder()
            .id(2L)
            .email("test@test.com")
            .nickname("nickname2")
            .memberStatusCode(MemberStatusCode.BASIC)
            .mogakkoTotalTime(0L)
            .githubId("github")
            .profileImage("image")
            .role(Role.USER)
            .socialUid("id")
            .socialType(SocialType.GOOGLE)
            .password("1q2w3e4r")
            .codingTem(36.5)
            .build();

    Notification notification = Notification.builder()
            .senderNickname("nickname")
            .receiverId(1L)
            .content("content")
            .url("url")
            .receiverNickname("receiverNickname")
            .type(NotificationType.LOGIN)
            .createdAt(Instant.ofEpochSecond(1_000_000_000))
            .readStatus(false)
            .build();



    @Nested
    @DisplayName("받은 알람 조회 테스트")
    class GetMyNotification {
        @DisplayName("받은 알람 조회 성공 테스트")
        @Test
        void getMyNotification_success() {
            // given
            List<Notification> notificationList = new ArrayList<>();
            notificationList.add(notification);

            when(notificationRepository.findAllByReceiverId(receiver.getId())).thenReturn(notificationList);

            // when
            ResponseEntity<Message> response = notificationSearchService.getMyNotification(receiver);

            // then
            List<NotificationResponseDto> responseDtoList = (List<NotificationResponseDto>) response.getBody().getData();
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("알림 조회 완료", response.getBody().getMessage());
            for (int i = 0; i < responseDtoList.size(); i++) {
                assertEquals(notificationList.get(i).getSenderNickname(), responseDtoList.get(i).getSenderNickname());
            }
        }

        @DisplayName("받은 알람 조회 결과 없음 테스트")
        @Test
        void getMyNotification_successWithEmptyResult() {
            // given
            List<Notification> notificationList = new ArrayList<>();

            when(notificationRepository.findAllByReceiverId(receiver.getId())).thenReturn(notificationList);

            // when
            ResponseEntity<Message> response = notificationSearchService.getMyNotification(receiver);

            // then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("알림이 없습니다.", response.getBody().getMessage());
        }
    }
}
package com.mogakko.be_final.domain.sse.service;

import com.mogakko.be_final.domain.members.entity.MemberStatusCode;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.entity.Role;
import com.mogakko.be_final.domain.members.entity.SocialType;
import com.mogakko.be_final.domain.sse.entity.NotificationType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

@ExtendWith({MockitoExtension.class})
class NotificationSendServiceTest {

    @Value("${SERVER_URL}")
    private String SERVER_URL;

    @Mock
    NotificationService notificationService;
    @InjectMocks
    NotificationSendService notificationSendService;

    Members sender = Members.builder()
            .id(1L)
            .email("test@example.com")
            .nickname("nickname")
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

    @Nested
    @DisplayName("sendFriendRequestNotification Method 테스트")
    class SendFriendRequestNotification {
        @DisplayName("sendFriendRequestNotification 성공 테스트")
        @Test
        void sendFriendRequestNotification() {
            // when
            notificationSendService.sendFriendRequestNotification(sender, receiver);

            // then
            verify(notificationService)
                    .send(sender, receiver, NotificationType.FRIEND_REQUEST, sender.getNickname() + "님이 친구요청을 보냈습니다.", SERVER_URL + "/friendship/requests/pending");
        }
    }

    @Nested
    @DisplayName("sendAcceptNotification Method 테스트")
    class SendAcceptNotification {
        @DisplayName("sendAcceptNotification 성공 테스트")
        @Test
        void sendAcceptNotification() {
            // when
            notificationSendService.sendAcceptNotification(sender, receiver);

            // then
            verify(notificationService)
                    .send(sender, receiver, NotificationType.FRIEND_REQUEST, sender.getNickname() + "님이 친구요청을 수락하셨습니다.", SERVER_URL + "/friendship/requests/accepted");
        }
    }

    @Nested
    @DisplayName("sendRefuseNotification Method 테스트")
    class SendRefuseNotification {
        @DisplayName("sendRefuseNotification 성공 테스트")
        @Test
        void sendAcceptNotification() {
            // when
            notificationSendService.sendRefuseNotification(sender, receiver);

            // then
            verify(notificationService)
                    .send(sender, receiver, NotificationType.FRIEND_REQUEST, sender.getNickname() + "님이 친구요청을 거절하셨습니다.", "/friend/request/determine");
        }
    }

    @Nested
    @DisplayName("sendMessageReceivedNotification Method 테스트")
    class SendMessageReceivedNotification {
        @DisplayName("sendMessageReceivedNotification 성공 테스트")
        @Test
        void sendAcceptNotification() {
            // when
            notificationSendService.sendMessageReceivedNotification(sender, receiver);

            // then
            verify(notificationService)
                    .send(sender, receiver, NotificationType.MESSAGE, sender.getNickname() + "님으로부터 쪽지가 도착했습니다.", SERVER_URL + "/directMessage/received");
        }
    }
}
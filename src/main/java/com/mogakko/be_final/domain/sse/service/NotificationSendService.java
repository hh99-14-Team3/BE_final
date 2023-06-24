package com.mogakko.be_final.domain.sse.service;

import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.sse.entity.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class NotificationSendService {
    private final NotificationService notificationService;

    @Value("${SERVER_URL}")
    private String SERVER_URL;

    public Mono<Void> sendFriendRequestNotification(Members sender, Members receiver) {
        String content = sender.getNickname() + "님이 친구요청을 보냈습니다.";
        String url = SERVER_URL + "/friendship/requests/pending";

        return notificationService.send(sender, receiver, NotificationType.FRIEND_REQUEST, content, url);
    }

    public Mono<Void> sendAcceptNotification(Members sender, Members receiver) {
        String content = sender.getNickname() + "님이 친구요청을 수락하셨습니다.";
        String url = SERVER_URL + "/friendship/requests/accepted";

        return notificationService.send(sender, receiver, NotificationType.FRIEND_REQUEST, content, url);
    }

    public Mono<Void> sendRefuseNotification(Members sender, Members receiver) {
        String content = sender.getNickname() + "님이 친구요청을 거절하셨습니다.";
        String url = "/friend/request/determine";

        return notificationService.send(sender, receiver, NotificationType.FRIEND_REQUEST, content, url);
    }

    public Mono<Void> sendMessageReceivedNotification(Members sender, Members receiver) {
        String content = sender.getNickname() + "님으로부터 쪽지가 도착했습니다.";
        String url = SERVER_URL + "/directMessage/received";

        return notificationService.send(sender, receiver, NotificationType.MESSAGE, content, url);
    }
}
package com.mogakko.be_final.domain.sse.service;

import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.sse.entity.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class NotificationSendService {
    private final NotificationService notificationService;

    public void sendLoginNotification(Members sender, Members receiver) {
        String content = receiver.getNickname() + " 님은 " + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME ) + "에 로그인 하셨습니다.";
        String url = "/members/login";
        notificationService.send(sender, receiver, NotificationType.LOGIN, content, url);
    }

    public void sendFriendRequestNotification(Members sender, Members receiver){
        String content = sender.getNickname() + "님이 친구요청을 보냈습니다.";
        String url = "/friend/request/determine";

        notificationService.send(sender, receiver, NotificationType.FRIEND_REQUEST, content, url);
    }

    public void sendAcceptNotification(Members sender, Members receiver){
        String content = receiver.getNickname() + "님이 친구요청을 수락하셨습니다.";
        String url = "/friend/request/determine";

        notificationService.send(sender, receiver, NotificationType.FRIEND_REQUEST, content, url);
    }

    public void sendRefuseNotification(Members sender, Members receiver){
        String content = receiver.getNickname() + "님이 친구요청을 거절하셨습니다.";
        String url = "/friend/request/determine";

        notificationService.send(sender, receiver, NotificationType.FRIEND_REQUEST, content, url);
    }




}

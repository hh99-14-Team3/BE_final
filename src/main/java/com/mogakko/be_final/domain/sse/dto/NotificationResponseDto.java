package com.mogakko.be_final.domain.sse.dto;

import com.mogakko.be_final.domain.sse.entity.Notification;
import com.mogakko.be_final.domain.sse.entity.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Schema(description = "알림 Dto")
@Getter
@NoArgsConstructor
public class NotificationResponseDto {
    private Long receiverId;
    private String content;
    private String url;
    private String receiverNickname;
    private NotificationType notificationType;


    public NotificationResponseDto(Notification notification) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.receiverId = notification.getKey().getReceiverId();
        this.content = notification.getContent();
        this.url = notification.getUrl();
        this.receiverNickname = notification.getReceiverNickname();
        this.notificationType = notification.getKey().getNotificationType();
    }

}

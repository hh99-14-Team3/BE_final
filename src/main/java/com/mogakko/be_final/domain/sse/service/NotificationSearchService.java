package com.mogakko.be_final.domain.sse.service;

import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.sse.dto.response.NotificationResponseDto;
import com.mogakko.be_final.domain.sse.entity.Notification;
import com.mogakko.be_final.domain.sse.repository.NotificationRepository;
import com.mogakko.be_final.exception.CustomException;
import com.mogakko.be_final.userDetails.UserDetailsImpl;
import com.mogakko.be_final.util.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.mogakko.be_final.exception.ErrorCode.NOTIFICATION_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class NotificationSearchService {
    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public ResponseEntity<Message> getMyNotification(UserDetailsImpl userDetails) {
        Members receiver = userDetails.getMember();

        List<Notification> notificationList = notificationRepository.findAllByReceiverId(receiver.getId());
        List<NotificationResponseDto> receivedNotificationList = new ArrayList<>();
        for (Notification notification : notificationList) {
            NotificationResponseDto receivedNotification = new NotificationResponseDto(notification);
            receivedNotificationList.add(receivedNotification);
        }


        if (!notificationList.isEmpty()) {
            return new ResponseEntity<>(new Message("알림 조회 완료", receivedNotificationList), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new Message("알림이 없습니다.", null), HttpStatus.OK);
        }


    }

    public ResponseEntity<Message> readNotification(Long memberId, UUID NotificationId){
        Notification findNotification= notificationRepository.findByReceiverIdAndReadStatusAndNotificationId(
                memberId, false,  NotificationId).orElseThrow(() -> new CustomException(NOTIFICATION_NOT_FOUND));
        markAsRead(findNotification);
        return new ResponseEntity<>(new Message("읽음 처리 완료", null), HttpStatus.OK);
    }

    public ResponseEntity<Message> readAllNotification(Long memberId){
        List<Notification> findNotificationList = notificationRepository.findAllByReceiverIdAndReadStatusFalse(memberId);

        if (!findNotificationList.isEmpty()){
            for (Notification notification : findNotificationList) {
                markAsRead(notification);
            }
            return new ResponseEntity<>(new Message("모두 읽음 처리 완료.",null), HttpStatus.OK);
        }else{
            return new ResponseEntity<>(new Message("읽지 않은 알림이 없습니다.",null), HttpStatus.OK);
        }
    }

    public void markAsRead(Notification notification){
        notificationRepository.delete(notification);
        notification.changeReadStatus();
        notificationRepository.save(notification);
    }
}

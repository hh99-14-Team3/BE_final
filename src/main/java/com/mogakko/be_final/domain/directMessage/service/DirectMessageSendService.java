package com.mogakko.be_final.domain.directMessage.service;

import com.mogakko.be_final.domain.directMessage.dto.DirectMessageSendRequestDto;
import com.mogakko.be_final.domain.directMessage.entity.DirectMessage;
import com.mogakko.be_final.domain.directMessage.repository.DirectMessageRepository;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.repository.MembersRepository;
import com.mogakko.be_final.domain.sse.service.NotificationSendService;
import com.mogakko.be_final.exception.CustomException;
import com.mogakko.be_final.exception.ErrorCode;
import com.mogakko.be_final.userDetails.UserDetailsImpl;
import com.mogakko.be_final.util.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DirectMessageSendService {
    private MembersRepository membersRepository;
    private DirectMessageRepository directMessageRepository;
    private NotificationSendService notificationSendService;

    public ResponseEntity<Message> sendDirectMessage(UserDetailsImpl userDetails, DirectMessageSendRequestDto directMessageSendRequestDto){
        Members messageSender = userDetails.getMember();
        Members messageReceiver = membersRepository.findByNickname(directMessageSendRequestDto.getMessageReceiverNickname()).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );
        String messageContent = directMessageSendRequestDto.getContent();

        saveMessage(messageSender, messageReceiver, messageContent);
        notificationSendService.sendMessageReceivedNotification(messageSender, messageReceiver);

        return new ResponseEntity<>(new Message("쪽지 전송 선공",null), HttpStatus.OK);

    }

    public void saveMessage(Members sender, Members receiver, String messageContent){
        DirectMessage directMessage = new DirectMessage(sender, receiver, messageContent, false);
        directMessageRepository.save(directMessage);
    }
}

package com.mogakko.be_final.domain.directMessage.service;

import com.mogakko.be_final.domain.directMessage.dto.DirectMessageSendRequestDto;
import com.mogakko.be_final.domain.directMessage.entity.DirectMessage;
import com.mogakko.be_final.domain.directMessage.repository.DirectMessageRepository;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.repository.MembersRepository;
import com.mogakko.be_final.domain.sse.service.NotificationSendService;
import com.mogakko.be_final.exception.CustomException;
import com.mogakko.be_final.exception.ErrorCode;
import com.mogakko.be_final.util.BadWordFiltering;
import com.mogakko.be_final.util.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.mogakko.be_final.exception.ErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class DirectMessageSendService {
    private final BadWordFiltering badWordFiltering;
    private final NotificationSendService notificationSendService;
    private final MembersRepository membersRepository;
    private final DirectMessageRepository directMessageRepository;

    // 쪽지 전송
    @Transactional
    public ResponseEntity<Message> sendDirectMessage(Members member, DirectMessageSendRequestDto directMessageSendRequestDto) {
        Members messageReceiver = membersRepository.findByNickname(directMessageSendRequestDto.getMessageReceiverNickname()).orElseThrow(
                () -> new CustomException(USER_NOT_FOUND)
        );
        String messageContent = badWordFiltering.checkBadWord(directMessageSendRequestDto.getContent());

        if (messageReceiver.getNickname().equals(member.getNickname())) {
            return new ResponseEntity<>(new Message("자신에게는 쪽지를 보낼 수 없습니다.", null), HttpStatus.BAD_REQUEST);
        }

        if (messageContent.isEmpty()) {
            return new ResponseEntity<>(new Message("내용을 입력해 주세요.", null), HttpStatus.BAD_REQUEST);
        }

        DirectMessage directMessage = new DirectMessage(member, messageReceiver, messageContent, false);
        directMessageRepository.save(directMessage);
        notificationSendService.sendMessageReceivedNotification(member, messageReceiver);

        return new ResponseEntity<>(new Message("쪽지 전송 선공", null), HttpStatus.OK);
    }
}

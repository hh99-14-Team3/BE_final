package com.mogakko.be_final.domain.directMessage.service;

import com.mogakko.be_final.domain.directMessage.dto.request.DirectMessageSendRequestDto;
import com.mogakko.be_final.domain.directMessage.entity.DirectMessage;
import com.mogakko.be_final.domain.directMessage.repository.DirectMessageRepository;
import com.mogakko.be_final.domain.directMessage.util.DirectMessageServiceUtilMethod;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.repository.MembersRepository;
import com.mogakko.be_final.domain.sse.service.NotificationSendService;
import com.mogakko.be_final.exception.CustomException;
import com.mogakko.be_final.util.BadWordFiltering;
import com.mogakko.be_final.util.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.mogakko.be_final.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class DirectMessagePostService {

    private final BadWordFiltering badWordFiltering;
    private final NotificationSendService notificationSendService;
    private final MembersRepository membersRepository;
    private final DirectMessageRepository directMessageRepository;
    private final DirectMessageServiceUtilMethod directMessageServiceUtilMethod;

    @Transactional
    public ResponseEntity<Message> sendDirectMessage(Members member, DirectMessageSendRequestDto directMessageSendRequestDto) {
        Optional<Members> messageReceiver;
        String userInfo = directMessageSendRequestDto.getMessageReceiverNickname();

        if (userInfo.length() == 6) {
            try {
                int friendCode = Integer.parseInt(userInfo);
                messageReceiver = membersRepository.findByFriendCode(friendCode);
            } catch (NumberFormatException e) {
                messageReceiver = membersRepository.findByNickname(userInfo);
            }
        } else {
            messageReceiver = membersRepository.findByNickname(userInfo);
        }

        Members receiver;
        if (messageReceiver.isPresent()) receiver = messageReceiver.get();
        else throw new CustomException(USER_NOT_FOUND);

        String messageContent = badWordFiltering.checkBadWord(directMessageSendRequestDto.getContent());

        if (receiver.getNickname().equals(member.getNickname())) throw new CustomException(CANNOT_REQUEST);
        if (messageContent.isEmpty()) throw new CustomException(PLZ_INPUT);

        DirectMessage directMessage = new DirectMessage(member, receiver, messageContent, false);
        directMessageRepository.save(directMessage);
        notificationSendService.sendMessageReceivedNotification(member, receiver).subscribe();

        return new ResponseEntity<>(new Message("쪽지 전송 성공", null), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Message> deleteDirectMessage(Members member, List<Long> messageIdList) {

        for (Long messageId : messageIdList) {
            DirectMessage directMessage = directMessageServiceUtilMethod.findDirectMessageById(messageId);

            if (directMessage.getReceiver().getNickname().equals(member.getNickname()) && !directMessage.isDeleteByReceiver()) {
                directMessage.markDeleteByReceiverTrue();

                if (directMessage.isDeleteBySender()) {
                    directMessageRepository.delete(directMessage);
                } else {
                    directMessageRepository.save(directMessage);
                }
            } else if (directMessage.getSender().getNickname().equals(member.getNickname()) && !directMessage.isDeleteBySender()) {
                directMessage.markDeleteBySenderTrue();

                if (directMessage.isDeleteByReceiver()) {
                    directMessageRepository.delete(directMessage);
                } else {
                    directMessageRepository.save(directMessage);
                }
            } else {
                throw new CustomException(USER_MISMATCH_ERROR);
            }
        }
        return new ResponseEntity<>(new Message("쪽지 삭제가 완료되었습니다.", null), HttpStatus.OK);
    }
}

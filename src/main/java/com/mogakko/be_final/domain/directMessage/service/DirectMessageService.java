package com.mogakko.be_final.domain.directMessage.service;

import com.mogakko.be_final.domain.directMessage.dto.request.DirectMessageSendRequestDto;
import com.mogakko.be_final.domain.directMessage.dto.response.DirectMessageSearchResponseDto;
import com.mogakko.be_final.domain.directMessage.entity.DirectMessage;
import com.mogakko.be_final.domain.directMessage.repository.DirectMessageRepository;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.repository.MembersRepository;
import com.mogakko.be_final.domain.sse.service.NotificationSendService;
import com.mogakko.be_final.exception.CustomException;
import com.mogakko.be_final.util.BadWordFiltering;
import com.mogakko.be_final.util.Message;
import jnr.a64asm.Mem;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mogakko.be_final.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class DirectMessageService {
    private final BadWordFiltering badWordFiltering;
    private final NotificationSendService notificationSendService;
    private final MembersRepository membersRepository;
    private final DirectMessageRepository directMessageRepository;

    // 쪽지 전송
    @Transactional
    public ResponseEntity<Message> sendDirectMessage(Members member, DirectMessageSendRequestDto directMessageSendRequestDto) {
        Optional<Members> messageReceiver;
        String userInfo = directMessageSendRequestDto.getMessageReceiverNickname();

        if (userInfo.length() == 6){
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


    @Transactional(readOnly = true)
    public ResponseEntity<Message> searchReceivedMessage(Members member) {
        List<DirectMessageSearchResponseDto> messageList = new ArrayList<>();

        List<DirectMessage> list = directMessageRepository.findAllByReceiverAndDeleteByReceiverFalse(member);

        for (DirectMessage directMessage : list) {
            DirectMessageSearchResponseDto message = new DirectMessageSearchResponseDto(directMessage);
            messageList.add(message);
        }


        if (messageList.isEmpty()) {
            return new ResponseEntity<>(new Message("도착한 쪽지가 없습니다.", null), HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(new Message("쪽지 목록 조회 완료", messageList), HttpStatus.OK);
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Message> searchSentMessage(Members member) {
        List<DirectMessageSearchResponseDto> messageList = new ArrayList<>();

        List<DirectMessage> list = directMessageRepository.findAllBySenderAndDeleteBySenderFalse(member);

        for (DirectMessage directMessage : list) {
            DirectMessageSearchResponseDto message = new DirectMessageSearchResponseDto(directMessage);
            messageList.add(message);
        }

        if (messageList.isEmpty()) {
            return new ResponseEntity<>(new Message("보낸 쪽지가 없습니다.", null), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new Message("쪽지 목록 조회 완료", messageList), HttpStatus.OK);
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Message> readDirectMessage(Members member, Long messageId) {
        DirectMessage findMessage = findDirectMessageById(messageId);

        if (member.getNickname().equals(findMessage.getReceiver().getNickname()))  {
            findMessage.markRead();
            return new ResponseEntity<>(new Message("쪽지 조회 완료", findMessage), HttpStatus.OK);
        } else {
            throw new CustomException(USER_MISMATCH_ERROR);
        }
    }

    @Transactional
    public ResponseEntity<Message> deleteDirectMessage(Members member, List<Long> messageIdList){

        for (Long messageId : messageIdList) {
            DirectMessage directMessage = findDirectMessageById(messageId);

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

    /**
     * Method
     */

    private DirectMessage findDirectMessageById(Long id) {
        return directMessageRepository.findById(id).orElseThrow(
                () -> new CustomException(MESSAGE_NOT_FOUND)
        );
    }
}

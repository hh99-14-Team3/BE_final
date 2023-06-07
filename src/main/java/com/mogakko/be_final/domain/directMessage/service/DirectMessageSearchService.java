package com.mogakko.be_final.domain.directMessage.service;

import com.mogakko.be_final.domain.directMessage.dto.DirectMessageSearchResponseDto;
import com.mogakko.be_final.domain.directMessage.entity.DirectMessage;
import com.mogakko.be_final.domain.directMessage.repository.DirectMessageRepository;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.exception.CustomException;
import com.mogakko.be_final.exception.ErrorCode;
import com.mogakko.be_final.util.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectMessageSearchService {
    private final DirectMessageRepository directMessageRepository;

    @Transactional(readOnly = true)
    public ResponseEntity<Message> searchReceivedMessage(Members member) {
        List<DirectMessageSearchResponseDto> messageList = new ArrayList<>();

        List<DirectMessage> list = directMessageRepository.findAllByReceiver(member);

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

        List<DirectMessage> list = directMessageRepository.findAllBySender(member);

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
        DirectMessage findMessage = directMessageRepository.findById(messageId).orElseThrow(
                () -> new CustomException(ErrorCode.MESSAGE_NOT_FOUND)
        );
        if (member == findMessage.getReceiver()) {
            findMessage.markRead();
            return new ResponseEntity<>(new Message("쪽지 조회 완료", findMessage), HttpStatus.OK);
        } else {
            throw new CustomException(ErrorCode.INTERNAL_SERER_ERROR);
        }
    }
}

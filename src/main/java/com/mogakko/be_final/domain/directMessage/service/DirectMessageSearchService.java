package com.mogakko.be_final.domain.directMessage.service;

import com.mogakko.be_final.domain.directMessage.entity.DirectMessage;
import com.mogakko.be_final.domain.directMessage.repository.DirectMessageRepository;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.exception.CustomException;
import com.mogakko.be_final.exception.ErrorCode;
import com.mogakko.be_final.userDetails.UserDetailsImpl;
import com.mogakko.be_final.util.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectMessageSearchService {
    private DirectMessageRepository directMessageRepository;

    public ResponseEntity<Message> searchReceivedMessage(UserDetailsImpl userDetails){
        Members member = userDetails.getMember();
        List<DirectMessage> messageList = directMessageRepository.findAllByReceiver(member);
        if(messageList.isEmpty()){
            return new ResponseEntity<>(new Message("도착한 쪽지가 없습니다.", null), HttpStatus.NOT_FOUND);
        }else {
            return new ResponseEntity<>(new Message("쪽지 목록 조회 완료", messageList), HttpStatus.OK);
        }
    }

    public ResponseEntity<Message> searchSentMessage(UserDetailsImpl userDetails){
        Members member = userDetails.getMember();
        List<DirectMessage> messageList = directMessageRepository.findAllBySender(member);
        if(messageList.isEmpty()){
            return new ResponseEntity<>(new Message("도착한 쪽지가 없습니다.", null), HttpStatus.OK);
        }else {
            return new ResponseEntity<>(new Message("쪽지 목록 조회 완료", messageList), HttpStatus.OK);
        }
    }

    public ResponseEntity<Message> readDirectMessage(UserDetailsImpl userDetails, Long messageId){
        Members member = userDetails.getMember();
        DirectMessage findMessage = directMessageRepository.findById(messageId).orElseThrow(
                () -> new CustomException(ErrorCode.MESSAGE_NOT_FOUND)
        );
        if(member==findMessage.getReceiver()){
            findMessage.markRead();
            return new ResponseEntity<>(new Message("쪽지 목록 조회 완료", findMessage), HttpStatus.OK);
        }else{
            throw new CustomException(ErrorCode.INTERNAL_SERER_ERROR);
        }
    }
}

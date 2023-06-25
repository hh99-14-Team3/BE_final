package com.mogakko.be_final.domain.mogakkoRoom.service;

import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.mogakkoRoom.entity.MogakkoRoom;
import com.mogakko.be_final.domain.mogakkoRoom.entity.MogakkoRoomMembers;
import com.mogakko.be_final.domain.mogakkoRoom.repository.MogakkoRoomMembersRepository;
import com.mogakko.be_final.domain.mogakkoRoom.repository.MogakkoRoomRepository;
import com.mogakko.be_final.exception.CustomException;
import com.mogakko.be_final.util.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.mogakko.be_final.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class MogakkoDeleteService {

    private final MogakkoRoomRepository mogakkoRoomRepository;
    private final MogakkoRoomMembersRepository mogakkoRoomMembersRepository;

    // 모각코 방 퇴장
    @Transactional
    public ResponseEntity<Message> outMogakko(String sessionId, Members members) {

        // 모각코 방 존재 확인
        MogakkoRoom mogakkoRoom = mogakkoRoomRepository.findBySessionId(sessionId).orElseThrow(
                () -> new CustomException(MOGAKKO_NOT_FOUND)
        );

        // 방에 멤버가 존재하는지 확인
        MogakkoRoomMembers mogakkoRoomMembers = mogakkoRoomMembersRepository.findByMemberIdAndMogakkoRoomAndIsEntered(members.getId(), mogakkoRoom, true).orElseThrow(
                () -> new CustomException(NOT_MOGAKKO_MEMBER)
        );

        // 유저가 이미 방에서 나감
        if (!mogakkoRoomMembers.isEntered()) {
            throw new CustomException(ALREADY_OUT_MEMBER);
        }

        // 모각코 유저 퇴장 처리
        mogakkoRoomMembers.deleteRoomMembers();

        // 모각코 유저 수 확인
        // 모각코 유저가 0명이라면 방 논리삭제
        synchronized (mogakkoRoom) {
            // 방 인원 카운트 - 1
            mogakkoRoom.updateCntMembers(mogakkoRoom.getCntMembers() - 1);
            mogakkoRoomMembers.isEntered();
            if (mogakkoRoom.getCntMembers() <= 0) {
                // 모각코 삭제처리
                mogakkoRoom.deleteRoom();
                return new ResponseEntity<>(new Message("모각코 퇴장 및 방 삭제 성공", null), HttpStatus.OK);
            }
            // 모각코의 유저 수가 1명 이상있다면 유저 수만 변경
            return new ResponseEntity<>(new Message("모각코 퇴장 성공", null), HttpStatus.OK);
        }
    }
}

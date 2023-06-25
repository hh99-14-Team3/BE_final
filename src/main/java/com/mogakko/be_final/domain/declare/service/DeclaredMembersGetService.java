package com.mogakko.be_final.domain.declare.service;

import com.mogakko.be_final.domain.declare.entity.DeclaredMembers;
import com.mogakko.be_final.domain.declare.repository.DeclaredMembersRepository;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.entity.Role;
import com.mogakko.be_final.exception.CustomException;
import com.mogakko.be_final.util.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.mogakko.be_final.exception.ErrorCode.NOT_ADMIN;

@Service
@RequiredArgsConstructor
public class DeclaredMembersGetService {
    private final DeclaredMembersRepository declaredMembersRepository;

    // 관리자 페이지 연결 (신고된 유저 확인)
    public ResponseEntity<Message> getReportedMembers(Members member) {
        Role role = member.getRole();
        if (role != Role.ADMIN) throw new CustomException(NOT_ADMIN);
        List<DeclaredMembers> declaredMembersList = declaredMembersRepository.findAll();
        return new ResponseEntity<>(new Message("신고된 멤버 조회 성공", declaredMembersList), HttpStatus.OK);
    }
}

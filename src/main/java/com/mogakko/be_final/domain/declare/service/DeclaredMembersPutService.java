package com.mogakko.be_final.domain.declare.service;

import com.mogakko.be_final.domain.declare.entity.DeclaredMembers;
import com.mogakko.be_final.domain.declare.repository.DeclaredMembersRepository;
import com.mogakko.be_final.domain.members.entity.MemberStatusCode;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.repository.MembersRepository;
import com.mogakko.be_final.exception.CustomException;
import com.mogakko.be_final.util.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.mogakko.be_final.domain.members.entity.Role.PROHIBITION;
import static com.mogakko.be_final.exception.ErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class DeclaredMembersPutService {
    private final DeclaredMembersRepository declaredMembersRepository;
    private final MembersRepository membersRepository;

    // 관리자 페이지 연결 (신고 적용)
    @Transactional
    public ResponseEntity<Message> handleReport(Long id, Members member) {
        DeclaredMembers findMember = declaredMembersRepository.findByDeclaredMemberId(id).orElseThrow(
                () -> new CustomException(USER_NOT_FOUND)
        );
        Members foundMember = findMember.getDeclaredMember();
        foundMember.declare();
        int declareCnt = foundMember.getDeclared() + 1;
        if (declareCnt == 1) foundMember.changeMemberStatusCode(MemberStatusCode.BAD_REQUEST);
        if (declareCnt == 2) foundMember.changeMemberStatusCode(MemberStatusCode.BAD_GATE_WAY);
        if (declareCnt == 3) {
            foundMember.changeMemberStatusCode(MemberStatusCode.BAD3);
            foundMember.changeRole(PROHIBITION);
        }
        membersRepository.save(foundMember);
        return new ResponseEntity<>(new Message("신고 처리 완료", null), HttpStatus.OK);
    }
}

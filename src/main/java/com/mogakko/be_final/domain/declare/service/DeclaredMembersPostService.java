package com.mogakko.be_final.domain.declare.service;

import com.mogakko.be_final.domain.declare.dto.request.DeclareRequestDto;
import com.mogakko.be_final.domain.declare.entity.DeclaredMembers;
import com.mogakko.be_final.domain.declare.entity.DeclaredReason;
import com.mogakko.be_final.domain.declare.repository.DeclaredMembersRepository;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.util.MembersServiceUtilMethod;
import com.mogakko.be_final.exception.CustomException;
import com.mogakko.be_final.util.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.mogakko.be_final.exception.ErrorCode.CANNOT_REQUEST;
import static com.mogakko.be_final.exception.ErrorCode.PLZ_INPUT_REASON_OF_REPORT;

@Service
@RequiredArgsConstructor
public class DeclaredMembersPostService {
    private final DeclaredMembersRepository declaredMembersRepository;
    private final MembersServiceUtilMethod membersServiceUtilMethod;

    // 회원 신고
    @Transactional
    public ResponseEntity<Message> declareMember(DeclareRequestDto declareRequestDto, Members member) {
        String declaredNickname = declareRequestDto.getDeclaredNickname();
        DeclaredReason declaredReason = declareRequestDto.getDeclaredReason();
        String reason = declareRequestDto.getReason();

        Members findMember = membersServiceUtilMethod.findMemberByNickname(declaredNickname);

        String reportedMemberNickname = member.getNickname();

        if (findMember.getNickname().equals(reportedMemberNickname)) throw new CustomException(CANNOT_REQUEST);
        if (declaredReason.equals(DeclaredReason.ETC) && reason.equals(""))
            throw new CustomException(PLZ_INPUT_REASON_OF_REPORT);
        declaredMembersRepository.save(DeclaredMembers.builder()
                .reporterNickname(reportedMemberNickname)
                .declaredMember(findMember)
                .declaredReason(declaredReason)
                .reason(reason).build());
        return new ResponseEntity<>(new Message("멤버 신고 성공", null), HttpStatus.OK);
    }
}

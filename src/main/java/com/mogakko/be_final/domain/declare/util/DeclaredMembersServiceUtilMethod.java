package com.mogakko.be_final.domain.declare.util;

import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.repository.MembersRepository;
import com.mogakko.be_final.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.mogakko.be_final.exception.ErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class DeclaredMembersServiceUtilMethod {
    private final MembersRepository membersRepository;
    public Members findMember(String nickname) {
        return membersRepository.findByNickname(nickname).orElseThrow(
                () -> new CustomException(USER_NOT_FOUND)
        );
    }
}

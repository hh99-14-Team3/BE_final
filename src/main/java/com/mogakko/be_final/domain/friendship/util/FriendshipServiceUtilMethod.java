package com.mogakko.be_final.domain.friendship.util;

import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.repository.MembersRepository;
import com.mogakko.be_final.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.mogakko.be_final.exception.ErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class FriendshipServiceUtilMethod {

    private final MembersRepository membersRepository;

    public Members findMemberByNickname(String memberNickname) {
        return membersRepository.findByNickname(memberNickname).orElseThrow(
                () -> new CustomException(USER_NOT_FOUND)
        );
    }

    public Members findMemberByFriendCode(Integer friendCode) {
        return membersRepository.findByFriendCode(friendCode).orElseThrow(
                () -> new CustomException(USER_NOT_FOUND)
        );
    }

    public String formatSeconds(Long remainingTime) {
        long hours = remainingTime / 3600;
        long minutes = (remainingTime % 3600) / 60;
        long seconds = remainingTime % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}

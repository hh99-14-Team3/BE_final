package com.mogakko.be_final.domain.members.dto.response;

import com.mogakko.be_final.domain.members.entity.Members;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserPageResponseDto {

    private Members member;
    private String totalTimer;
    private Boolean isFriend;

    public UserPageResponseDto(Members member, String totalTimer) {
        this.member = member;
        this.totalTimer = totalTimer;
    }

    public UserPageResponseDto(Members member, String totalTimer, boolean isFriend) {
        this.member = member;
        this.totalTimer = totalTimer;
        this.isFriend = isFriend;
    }
}

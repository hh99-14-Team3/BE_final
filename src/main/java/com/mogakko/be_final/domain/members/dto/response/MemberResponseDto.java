package com.mogakko.be_final.domain.members.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberResponseDto {
    private String nickname;
    private String profileImage;
    private boolean isTutorialCheck;

    public MemberResponseDto(String nickname, String profileImage, boolean isTutorialCheck) {
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.isTutorialCheck = isTutorialCheck;
    }
}

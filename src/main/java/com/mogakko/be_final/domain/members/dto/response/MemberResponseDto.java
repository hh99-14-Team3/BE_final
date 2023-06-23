package com.mogakko.be_final.domain.members.dto.response;

import com.mogakko.be_final.domain.members.entity.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberResponseDto {
    private String nickname;
    private String profileImage;
    private boolean isTutorialCheck;
    private Role role;

    public MemberResponseDto(String nickname, String profileImage, boolean isTutorialCheck, Role role) {
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.isTutorialCheck = isTutorialCheck;
        this.role = role;
    }
}

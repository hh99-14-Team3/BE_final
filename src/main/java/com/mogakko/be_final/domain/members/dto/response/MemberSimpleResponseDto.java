package com.mogakko.be_final.domain.members.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberSimpleResponseDto {
    private Long id;
    private String nickname;
    private String profileImage;
    private boolean isFriend;

    public MemberSimpleResponseDto(Long id, String nickname, String profileImage, boolean isFriend) {
        this.id = id;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.isFriend = isFriend;
    }
}

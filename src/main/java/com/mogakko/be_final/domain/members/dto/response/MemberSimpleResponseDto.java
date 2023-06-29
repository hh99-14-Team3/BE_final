package com.mogakko.be_final.domain.members.dto.response;

import com.mogakko.be_final.domain.members.entity.Members;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberSimpleResponseDto {
    private Long id;
    private String nickname;
    private String profileImage;
    private boolean isFriend;
    private boolean isPending;

    public MemberSimpleResponseDto(Members member, boolean isFriend, boolean isPending) {
        this.id = member.getId();
        this.nickname = member.getNickname();
        this.profileImage = member.getProfileImage();
        this.isFriend = isFriend;
        this.isPending = isPending;
    }
}

package com.mogakko.be_final.domain.friendship.dto.response;

import com.mogakko.be_final.domain.members.entity.Members;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class FriendResponseDto {

    private Members member;
    private boolean isSelected;

    public FriendResponseDto(Members member, boolean isSelected) {
        this.member = member;
        this.isSelected = isSelected;
    }
}

package com.mogakko.be_final.domain.friendship.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class DeleteFriendRequestDto {
    private List<String> receiverNickname;
}

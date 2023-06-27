package com.mogakko.be_final.domain.friendship.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendRequestByCodeDto {
    private int requestReceiverFriendCode;
}

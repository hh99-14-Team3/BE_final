package com.mogakko.be_final.domain.friendship.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendRequestDto {
    private String requestReceiverNickname;
}

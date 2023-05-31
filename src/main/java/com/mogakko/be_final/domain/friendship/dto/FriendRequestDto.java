package com.mogakko.be_final.domain.friendship.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FriendRequestDto {
    private String sender;
    private String receiver;
}

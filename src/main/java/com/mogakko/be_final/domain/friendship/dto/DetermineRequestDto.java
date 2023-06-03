package com.mogakko.be_final.domain.friendship.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DetermineRequestDto {
    private Long notificationId;
    private String senderNickname;
    private String receiverNickname;
    private boolean determineRequest; // true 친구수락, false 거절

    public DetermineRequestDto(Long notificationId, String senderNickname, String receiverNickname, boolean determineRequest) {
        this.notificationId = notificationId;
        this.senderNickname = senderNickname;
        this.receiverNickname = receiverNickname;
        this.determineRequest = determineRequest;
    }

}

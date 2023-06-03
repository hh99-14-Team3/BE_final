package com.mogakko.be_final.domain.friendship.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DetermineRequestDto {
    private Long notificationId;
    private Long senderId;
    private Long receiverId;
    private boolean determineRequest; // true 친구수락, false 거절

    public DetermineRequestDto(Long notificationId, Long senderId, Long receiverId, boolean determineRequest) {
        this.notificationId = notificationId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.determineRequest = determineRequest;
    }

}

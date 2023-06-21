package com.mogakko.be_final.domain.friendship.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DetermineRequestDto {
    private String requestSenderNickname; //알림 기준으로 sender
    private boolean determineRequest; // true 친구수락, false 거절

}

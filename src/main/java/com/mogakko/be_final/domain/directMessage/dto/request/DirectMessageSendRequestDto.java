package com.mogakko.be_final.domain.directMessage.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DirectMessageSendRequestDto {
    private String messageReceiverNickname;
    private String content;

}

package com.mogakko.be_final.domain.directMessage.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DirectMessageSendRequestDto {
    private String messageReceiverNickname;
    private String content;

}

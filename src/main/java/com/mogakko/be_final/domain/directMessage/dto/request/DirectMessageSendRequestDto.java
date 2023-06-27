package com.mogakko.be_final.domain.directMessage.dto.request;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DirectMessageSendRequestDto {
    private String messageReceiverNickname;
    private String content;
}

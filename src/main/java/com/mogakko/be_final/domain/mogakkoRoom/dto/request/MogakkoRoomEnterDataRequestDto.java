package com.mogakko.be_final.domain.mogakkoRoom.dto.request;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MogakkoRoomEnterDataRequestDto {
    private String password;
}
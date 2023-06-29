package com.mogakko.be_final.domain.mogakkoRoom.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MogakkoRoomEnterDataRequestDto {
    private String password;
}
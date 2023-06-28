package com.mogakko.be_final.domain.mogakkoRoom.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Mogakko12kmRequestDto {
    private double lon;
    private double lat;
}

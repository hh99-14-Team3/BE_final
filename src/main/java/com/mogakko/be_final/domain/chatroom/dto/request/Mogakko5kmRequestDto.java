package com.mogakko.be_final.domain.chatroom.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Mogakko5kmRequestDto {

    private double longitudeX;
    private double latitudeY;
    private String language;
}

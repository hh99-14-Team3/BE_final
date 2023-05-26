package com.mogakko.be_final.domain.mogakkoRoom.dto.request;

import com.mogakko.be_final.domain.mogakkoRoom.entity.LanguageEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Mogakko5kmRequestDto {

    private double longitudeX;
    private double latitudeY;
    private LanguageEnum language;
}

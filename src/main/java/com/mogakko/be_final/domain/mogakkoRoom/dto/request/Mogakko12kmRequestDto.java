package com.mogakko.be_final.domain.mogakkoRoom.dto.request;

import com.mogakko.be_final.domain.mogakkoRoom.entity.LanguageEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Mogakko12kmRequestDto {

    private double lon;
    private double lat;
    private LanguageEnum language;
}

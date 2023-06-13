package com.mogakko.be_final.domain.members.dto.response;

import com.mogakko.be_final.domain.mogakkoRoom.entity.LanguageEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LanguageDto {

    private LanguageEnum languageEnum;
    private double percentage;

    public LanguageDto(LanguageEnum languageEnum, long languageCnt, long totalCnt) {
        this.languageEnum = languageEnum;
        this.percentage = Math.round((double) languageCnt / totalCnt * 100.0 * 100) / 100.0;
    }
}
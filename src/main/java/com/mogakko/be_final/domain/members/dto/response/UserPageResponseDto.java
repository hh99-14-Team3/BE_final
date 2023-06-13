package com.mogakko.be_final.domain.members.dto.response;

import com.mogakko.be_final.domain.members.entity.Members;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
public class UserPageResponseDto {

    private Members member;
    private String totalTimer;
    private Map<String, String> timeOfWeek;
    private List<LanguageDto> languageList;
    private boolean isFriend;

    public UserPageResponseDto(Members member, String totalTimer, Map<String, String> timeOfWeek, List<LanguageDto> languageDtoList) {
        this.member = member;
        this.totalTimer = totalTimer;
        this.timeOfWeek = timeOfWeek;
        this.languageList = languageDtoList;
    }

    public UserPageResponseDto(Members member, String totalTimer, Map<String, String> timeOfWeek, List<LanguageDto> languageDtoList, boolean isFriend) {
        this.member = member;
        this.totalTimer = totalTimer;
        this.timeOfWeek = timeOfWeek;
        this.languageList = languageDtoList;
        this.isFriend = isFriend;
    }

}

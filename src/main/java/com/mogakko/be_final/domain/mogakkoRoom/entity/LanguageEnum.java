package com.mogakko.be_final.domain.mogakkoRoom.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

// 주특기 언어 카테고리
public enum LanguageEnum {

    JAVA("JAVA"), C("C"), CPLPL("C++"), CSHARP("C#"),
    PYTHON("PYTHON"), KOTLIN("KOTLIN"), JAVASCRIPT("JAVASCRIPT"), ETC("ETC");


    private final String language;

    LanguageEnum(String language) {
        this.language = language;
    }


    @JsonCreator
    public static LanguageEnum from(String value) {
        for (LanguageEnum status : LanguageEnum.values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }

    @JsonValue
    public String getValue() {
        return language;
    }
}

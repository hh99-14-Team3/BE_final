package com.mogakko.be_final.domain.chatroom.entity;

// 주특기 언어 카테고리
public enum LanguageEnum {

    JAVA("JAVA"), C_("C\\+\\+"), PYTHON("PYTHON"), RUBY("RUBY"),
    KOTLIN("KOTLIN"), SWIFT("SWIFT"), JAVASCRIPT("JAVASCRIPT"), GO("GO"),
    PHP("PHP"), RUST("RUST"), LUA("LUA"), C("C"), C__("C#");


    private final String language;

    LanguageEnum(String language) {
        this.language = language;
    }
}

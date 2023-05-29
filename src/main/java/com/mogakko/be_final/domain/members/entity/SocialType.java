package com.mogakko.be_final.domain.members.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SocialType {
    KAKAO("KAKAO"), GOOGLE("GOOGLE");

    private final String key;

}

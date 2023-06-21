package com.mogakko.be_final.domain.members.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    GUEST("ROLE_GUEST"), USER("ROLE_USER"), ADMIN("ADMIN");

    private final String key;
}

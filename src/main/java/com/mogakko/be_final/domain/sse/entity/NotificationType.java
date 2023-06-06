package com.mogakko.be_final.domain.sse.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {
    MESSAGE("MESSAGE"),
    FRIEND_REQUEST("FRIEND_REQUEST"),
    LOGIN("LOGIN");

    private final String key;
}

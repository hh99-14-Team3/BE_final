package com.mogakko.be_final.domain.friendship.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor
public class RejectedFriendship {

    private String member1Nickname;

    private String member2Nickname;

    private long rejectionTime;

    public RejectedFriendship(String member1Nickname, String member2Nickname) {
        this.member1Nickname = member1Nickname;
        this.member2Nickname = member2Nickname;
        this.rejectionTime = Instant.now().toEpochMilli();
    }
}

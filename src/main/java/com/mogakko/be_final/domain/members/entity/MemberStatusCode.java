package com.mogakko.be_final.domain.members.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum MemberStatusCode {
    BASIC("102"), NORMAL("200"), BAD_REQUEST("400"), BAD_GATE_WAY("502"), BAD3("401"), BYE("20000"),
    SPECIAL_ANGEL("1004"), SPECIAL_DOG("109"), SPECIAL_LOVE("486"), SPECIAL_LOVELOVE("2514"), EMOTICON("10000");

    private final String memberStatusCode;

    MemberStatusCode(String memberStatusCode) {
        this.memberStatusCode = memberStatusCode;
    }

    @JsonCreator
    public static MemberStatusCode from(String value) {
        for (MemberStatusCode status : MemberStatusCode.values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }

    @JsonValue
    public String getValue() {
        return memberStatusCode;
    }
}

/**
 * 102 : 회원가입 시 기본값
 * 200 : 처음 프로필 등록하면 변경
 * 400 : 신고 1회
 * 401 : 신고 2회
 * 404 : 신고 3회 (이용기간 30일 정지)
 *
 * @Special
 * 109 : 모각코 시간 1시간 9분 경과
 * 486 : 모각코 시간 4시간 8분 6초 경과
 * 1004 : 모각코 시간 10시간 4분 경과
 * 2514 : 모각코 시간 25시간 14분 경과
 *
 */


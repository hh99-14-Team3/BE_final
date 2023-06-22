package com.mogakko.be_final.domain.members.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum DeclaredReason {
    COMMERCIAL_PROMOTION("상업적/홍보성"), LEWDNESS("음란/선정성"), ILLEGAL("불법정보"),
    ABUSE("욕설/인신공격"), PRIVACY("개인정보노출"), ETC("기타");

    private final String declaredReason;

    DeclaredReason(String memberStatusCode) {
        this.declaredReason = memberStatusCode;
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
        return declaredReason;
    }
}

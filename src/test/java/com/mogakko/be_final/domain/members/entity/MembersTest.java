package com.mogakko.be_final.domain.members.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MembersTest {

    @DisplayName("setGithubStateCode Method 테스트")
    @Test
    void setGithubStateCode() {
        // given
        String statusCode = "statusCode";

        Members member = Members.builder().githubStateCode("code").build();

        // when
        member.setGithubStateCode(statusCode);

        // then
        assertEquals(statusCode, member.getGithubStateCode());
    }
}
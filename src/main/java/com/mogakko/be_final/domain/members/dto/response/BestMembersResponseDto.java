package com.mogakko.be_final.domain.members.dto.response;

import com.mogakko.be_final.domain.members.entity.Members;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@NoArgsConstructor
public class BestMembersResponseDto {
    private Members member;
    private Map<String, String> totalTimer;

    public BestMembersResponseDto(Members member, Map<String, String> totalTimer) {
        this.member = member;
        this.totalTimer = totalTimer;
    }
}

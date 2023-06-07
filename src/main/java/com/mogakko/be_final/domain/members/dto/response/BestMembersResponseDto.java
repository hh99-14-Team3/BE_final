package com.mogakko.be_final.domain.members.dto.response;

import com.mogakko.be_final.domain.members.entity.Members;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BestMembersResponseDto {
    private Members member;
    private String totalTimer;
    private String totalTimerWeek;

    public  BestMembersResponseDto(Members member, String totalTimer, String totalTimerWeek){
        this. member = member;
        this.totalTimer = totalTimer;
        this.totalTimerWeek = totalTimerWeek;
    }
}

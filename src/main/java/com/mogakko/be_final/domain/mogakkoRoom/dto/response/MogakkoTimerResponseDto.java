package com.mogakko.be_final.domain.mogakkoRoom.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Time;

@Getter
@NoArgsConstructor
public class MogakkoTimerResponseDto {
    private String mogakkoTimer;
    private String mogakkoWeekTimer;

    public MogakkoTimerResponseDto(String mogakkoTimer, String mogakkoWeekTimer){
        this.mogakkoTimer = mogakkoTimer;
        this.mogakkoWeekTimer = mogakkoWeekTimer;
    }
}

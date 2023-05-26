package com.mogakko.be_final.domain.mogakkoRoom.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DongNeResponseDto {

    private long count;
    private String dongNe;

    public DongNeResponseDto(long count, String dongNe) {
        this.count = count;
        this.dongNe = dongNe;
    }
}

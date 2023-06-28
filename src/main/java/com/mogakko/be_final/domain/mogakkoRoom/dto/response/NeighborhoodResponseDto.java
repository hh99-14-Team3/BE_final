package com.mogakko.be_final.domain.mogakkoRoom.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class NeighborhoodResponseDto {

    private long count;
    private String neighborhood;

    public NeighborhoodResponseDto(long count, String neighborhood) {
        this.count = count;
        this.neighborhood = neighborhood;
    }
}

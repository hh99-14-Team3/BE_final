package com.mogakko.be_final.domain.directMessage.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class DirectMessageDeleteRequestDto {
    private List<Long> directMessageList;
}

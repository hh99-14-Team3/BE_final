package com.mogakko.be_final.domain.members.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DeclareRequestDto {
    private String declaredNickname;
    private String declaredReason;
}

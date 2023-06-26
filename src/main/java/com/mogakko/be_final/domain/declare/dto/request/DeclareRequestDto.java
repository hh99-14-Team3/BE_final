package com.mogakko.be_final.domain.declare.dto.request;

import com.mogakko.be_final.domain.declare.entity.DeclaredReason;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeclareRequestDto {
    private String declaredNickname;
    @NotNull(message = "신고 이유를 선택해주세요.")
    private DeclaredReason declaredReason;
    private String reason;
}

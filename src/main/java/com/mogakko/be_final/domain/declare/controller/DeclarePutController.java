package com.mogakko.be_final.domain.declare.controller;

import com.mogakko.be_final.domain.declare.service.DeclaredMembersPutService;
import com.mogakko.be_final.userDetails.UserDetailsImpl;
import com.mogakko.be_final.util.Message;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
@Tag(name = "신고 관련 PUT 요청 API", description = "신고 관련 PUT 요청 API 입니다.")
public class DeclarePutController {
    private final DeclaredMembersPutService declaredMembersPutService;

    @Operation(summary = "신고 적용 API", description = "관리자가 신고를 적용하는 API입니다.")
    @PutMapping("/admin/ok/{declaredMemberId}")
    public ResponseEntity<Message> handleReport(@PathVariable Long declaredMemberId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return declaredMembersPutService.handleReport(declaredMemberId, userDetails.getMember());
    }

}

package com.mogakko.be_final.domain.declare.controller;

import com.mogakko.be_final.domain.declare.service.DeclaredMembersGetService;
import com.mogakko.be_final.userDetails.UserDetailsImpl;
import com.mogakko.be_final.util.Message;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
@Tag(name = "신고 관련 GET 요청 API", description = "신고 관련 GET 요청 API 입니다.")
public class DeclareGetController {
    private final DeclaredMembersGetService declaredMembersGetService;

    @Operation(summary = "유저 신고 조회 API", description = "신고된 유저를 조회하는 메서드입니다. (관리자 페이지)")
    @GetMapping("/admin")
    public ResponseEntity<Message> getReportedMembers(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return declaredMembersGetService.getReportedMembers(userDetails.getMember());
    }
}

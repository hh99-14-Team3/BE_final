package com.mogakko.be_final.domain.members.controller;

import com.mogakko.be_final.domain.members.service.MembersDeleteService;
import com.mogakko.be_final.userDetails.UserDetailsImpl;
import com.mogakko.be_final.util.Message;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
@Tag(name = "회원 관련 DELETE 요청 API", description = "회원 관련 DELETE 요청 API 입니다.")
public class MembersDeleteController {

    private final MembersDeleteService membersDeleteService;

    @Operation(summary = "회원 탈퇴 API", description = "회원 탈퇴하는 메서드입니다.")
    @DeleteMapping("/withdraw")
    public ResponseEntity<Message> withdrawMember(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return membersDeleteService.withdrawMember(userDetails.getMember());
    }
}

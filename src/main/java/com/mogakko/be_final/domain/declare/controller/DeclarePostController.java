package com.mogakko.be_final.domain.declare.controller;

import com.mogakko.be_final.domain.declare.dto.request.DeclareRequestDto;
import com.mogakko.be_final.domain.declare.service.DeclaredMembersPostService;
import com.mogakko.be_final.userDetails.UserDetailsImpl;
import com.mogakko.be_final.util.Message;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
@Tag(name = "신고 관련 POST 요청 API", description = "신고 관련 POST 요청 API 입니다.")
public class DeclarePostController {
    private final DeclaredMembersPostService declaredMembersPostService;

    @Operation(summary = "유저 신고 API", description = "선택한 유저를 신고하는 메서드입니다.")
    @PostMapping("/declare")
    public ResponseEntity<Message> declareMember(@Valid @RequestBody DeclareRequestDto declareRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return declaredMembersPostService.declareMember(declareRequestDto, userDetails.getMember());
    }
}

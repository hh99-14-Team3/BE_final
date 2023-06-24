package com.mogakko.be_final.domain.email.controller;

import com.mogakko.be_final.domain.email.dto.request.EmailConfirmRequestDto;
import com.mogakko.be_final.domain.email.service.EmailPostService;
import com.mogakko.be_final.domain.members.dto.request.ChangePwRequestDto;
import com.mogakko.be_final.util.Message;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
@Tag(name = "회원 관련 API", description = "회원 관련 API 입니다.")
public class EmailPostController {
    private final EmailPostService emailPostService;

    @Operation(summary = "이메일 전송", description = "비밀번호 찾기를 위한 이메일 전송 메서드입니다.")
    @PostMapping("/sendEmail")
    public ResponseEntity<Message> sendEmailToFindPassword(@RequestBody EmailConfirmRequestDto requestDto) throws Exception {
        return emailPostService.sendSimpleMessage(requestDto);
    }

    @Operation(summary = "비밀번호 변경", description = "이메일 확인 후 비밀번호 변경 메서드입니다.")
    @PostMapping("/updatePassword")
    public ResponseEntity<Message> confirmEmailToFindPassword(@Valid @RequestParam String token, @Valid @RequestBody ChangePwRequestDto requestDto) {
        return emailPostService.confirmEmailToFindPassword(token, requestDto);
    }
}

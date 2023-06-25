package com.mogakko.be_final.domain.members.controller;

import com.mogakko.be_final.domain.members.dto.request.GithubIdRequestDto;
import com.mogakko.be_final.domain.members.dto.request.LoginRequestDto;
import com.mogakko.be_final.domain.members.dto.request.SignupRequestDto;
import com.mogakko.be_final.domain.members.service.MembersPostService;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
@Tag(name = "회원 관련 API", description = "회원 관련 API 입니다.")
public class MembersPostController {

    private final MembersPostService membersPostService;

    @Operation(summary = "회원 가입 API", description = "회원가입하는 메서드입니다.")
    @PostMapping("/signup")
    public ResponseEntity<Message> signup(@Valid @RequestBody SignupRequestDto requestDto) {
        return membersPostService.signup(requestDto);
    }

    @Operation(summary = "로그인 API", description = "로그인하는 메서드입니다.")
    @PostMapping("/login")
    public ResponseEntity<Message> login(@RequestBody LoginRequestDto requestDto, HttpServletResponse httpServletResponse) {
        return membersPostService.login(requestDto, httpServletResponse);
    }

    @Operation(summary = "로그아웃 API", description = "로그아웃하는 메서드입니다.")
    @PostMapping("/logout")
    public ResponseEntity<Message> logout(@AuthenticationPrincipal UserDetailsImpl userDetails, HttpServletRequest request) {
        return membersPostService.logout(userDetails.getMember(), request);
    }

    @Operation(summary = "GitHub id 등록 API", description = "마이페이지에서 깃허브 아이디를 등록하는 메서드입니다.")
    @PostMapping("/github")
    public ResponseEntity<Message> addGithub(@RequestBody GithubIdRequestDto githubIdRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return membersPostService.addGithub(githubIdRequestDto, userDetails.getMember());
    }
}


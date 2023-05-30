package com.mogakko.be_final.domain.members.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mogakko.be_final.domain.members.dto.request.LoginRequestDto;
import com.mogakko.be_final.domain.members.dto.request.SignupRequestDto;
import com.mogakko.be_final.domain.members.service.MembersService;
import com.mogakko.be_final.kakao.KakaoService;
import com.mogakko.be_final.userDetails.UserDetailsImpl;
import com.mogakko.be_final.util.Message;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
@Tag(name = "회원 관련 API", description = "회원 관련 API 입니다.")
public class MembersController {
    private final MembersService membersService;
    private final KakaoService kakaoService;

    @PostMapping("/signup")
    @Operation(summary = "회원 가입 API", description = "회원가입하는 메서드입니다.")
    public ResponseEntity<Message> signup(@Valid @RequestBody SignupRequestDto requestDto, HttpSession session) {
        return membersService.signup(requestDto, session);
    }

    @GetMapping("/signup/checkEmail")
    @Operation(summary = "이메일 중복 체크 API", description = "이메일 중복 체크를 하는 메서드입니다.")
    public ResponseEntity<Message> checkEmail(@RequestParam("email") String email, HttpSession session) {
        session.setAttribute("emailChecked", true);
        return membersService.checkEmail(email);
    }

    @GetMapping("/signup/checkNickname")
    @Operation(summary = "닉네임 중복 체크 API", description = "닉네임 중복 체크를 하는 메서드입니다.")
    public ResponseEntity<Message> checkNickname(@RequestParam("nickname") String nickname, HttpSession session) {
        session.setAttribute("nicknameChecked", true);
        return membersService.checkNickname(nickname);
    }

    @PostMapping("/login")
    @Operation(summary = "로그인 API", description = "로그인하는 메서드입니다.")
    public ResponseEntity<Message> login(@RequestBody LoginRequestDto requestDto, HttpServletResponse httpServletResponse) {
        return membersService.login(requestDto, httpServletResponse);
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃 API", description = "로그아웃하는 메서드입니다.")
    public ResponseEntity<Message> logout(@AuthenticationPrincipal UserDetailsImpl userDetails, HttpServletRequest request) {
        return membersService.logout(userDetails.getMember(), request);
    }

    @GetMapping("/kakaoLogin")
    @Operation(summary = "카카오 소셜 로그인 API", description = "카카오로 소셜 로그인을 하는 메서드입니다.")
    public ResponseEntity<Message> kakaoLogin(@RequestParam("code") String code, HttpServletResponse response) throws JsonProcessingException {
        return kakaoService.kakaoLogin(code, response);
    }

    @GetMapping("/mypage")
    @Operation(summary = "마이페이지 API", description = "마이페이지에서 '참여중인 모각코방', '총 참여 시간'을 보여주는 메서드입니다.")
    public ResponseEntity<Message> myPage(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return membersService.readMyPage(userDetails.getMember());
    }

    @Operation(summary = "마이페이지 수정 API", description = "마이페이지에서 프로필 사진을 수정하는 메서드입니다.")
    @PutMapping(value = "/mypage", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Message> profileUpdate(@RequestPart("imageFile") MultipartFile image, @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        return membersService.profileUpdate(image, userDetails.getMember());
    }
}


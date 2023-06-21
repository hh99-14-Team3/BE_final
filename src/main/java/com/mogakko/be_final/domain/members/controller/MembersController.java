package com.mogakko.be_final.domain.members.controller;

import com.mogakko.be_final.domain.members.dto.request.GithubIdRequestDto;
import com.mogakko.be_final.domain.members.dto.request.LoginRequestDto;
import com.mogakko.be_final.domain.members.dto.request.SignupRequestDto;
import com.mogakko.be_final.domain.members.service.MembersService;
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
import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
@Tag(name = "회원 관련 API", description = "회원 관련 API 입니다.")
public class MembersController {
    private final MembersService membersService;

    @PostMapping("/signup")
    @Operation(summary = "회원 가입 API", description = "회원가입하는 메서드입니다.")
    public ResponseEntity<Message> signup(@Valid @RequestBody SignupRequestDto requestDto) {
        return membersService.signup(requestDto);
    }

    @GetMapping("/signup/checkEmail")
    @Operation(summary = "이메일 중복 체크 API", description = "이메일 중복 체크를 하는 메서드입니다.")
    public ResponseEntity<Message> checkEmail(@RequestParam("email") String email) {
        return membersService.checkEmail(email);
    }

    @GetMapping("/signup/checkNickname")
    @Operation(summary = "닉네임 중복 체크 API", description = "닉네임 중복 체크를 하는 메서드입니다.")
    public ResponseEntity<Message> checkNickname(@RequestParam("nickname") String nickname) {
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

    @GetMapping("/mypage")
    @Operation(summary = "마이페이지 API", description = "마이페이지에서 '참여중인 모각코방', '총 참여 시간'을 보여주는 메서드입니다.")
    public ResponseEntity<Message> myPage(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return membersService.readMyPage(userDetails.getMember());
    }

    @PostMapping("/github")
    @Operation(summary = "GitHub id 등록 API", description = "마이페이지에서 깃허브 아이디를 등록하는 메서드입니다.")
    public ResponseEntity<Message> addGithub(@RequestBody GithubIdRequestDto githubIdRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return membersService.addGithub(githubIdRequestDto, userDetails.getMember());
    }

    @Operation(summary = "마이페이지 수정 API", description = "마이페이지에서 정보(프로필 사진, 닉네임)를 변경하는 메서드입니다.")
    @PutMapping(value = "/mypage", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Message> profileUpdate(@RequestPart(value = "imageFile", required = false) MultipartFile image,
                                                 @RequestPart(value = "nickname", required = false) String nickname,
                                                 @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        return membersService.profileUpdate(image, nickname, userDetails.getMember());
    }

    @Operation(summary = "마이페이지 프로필사진 삭제 API", description = "마이페이지에서 프로필 사진을 삭제하는 메서드입니다.")
    @PutMapping("/mypage/delete")
    public ResponseEntity<Message> profileDelete(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return membersService.profileDelete(userDetails.getMember());
    }

    @Operation(summary = "다른 유저 프로필 조회 API", description = "다른 유저의 프로필을 조회하는 메서드입니다.")
    @GetMapping("/{memberId}")
    public ResponseEntity<Message> getMemberProfile(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long memberId) {
        return membersService.getMemberProfile(userDetails.getMember(), memberId);
    }

    @Operation(summary = "다른 유저 닉네임 검색 API", description = "다른 유저를 닉네임으로 검색하는 메서드입니다.")
    @GetMapping("/search/nickname")
    public ResponseEntity<Message> searchMembersByNickname(@RequestParam String nickname, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return membersService.searchMembersByNickname(nickname, userDetails.getMember());
    }

    @Operation(summary = "다른 유저 코드 검색 API", description = "다른 유저를 코드로 검색하는 메서드입니다.")
    @GetMapping("/search/friend-code")
    public ResponseEntity<Message> searchMemberByFriendsCode(@RequestParam String friendCode, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return membersService.searchMemberByFriendsCode(friendCode, userDetails.getMember());
    }

    @Operation(summary = "최고의 유저 조회 API", description = "순공 시간이 가장 높은 모각코인을 조회하는 메서드입니다.")
    @GetMapping("/best")
    public ResponseEntity<Message> readBestMembers() {
        return membersService.readBestMembers();
    }

    @Operation(summary = "유저 신고 API", description = "선택한 유저를 신고하는 메서드입니다.")
    @PutMapping("/declare")
    public ResponseEntity<Message> declareMember(@RequestParam Long memberId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return membersService.declareMember(memberId, userDetails.getMember());
    }
  
    @Operation(summary = "튜토리얼 체크 메서드 API", description = "유저가 튜토리얼을 확인했는지 체크하는 메서드입니다.")
    @PutMapping("/tutorialCheck")
    public ResponseEntity<Message> tutorialCheck(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return membersService.tutorialCheck(userDetails.getMember());
    }
}


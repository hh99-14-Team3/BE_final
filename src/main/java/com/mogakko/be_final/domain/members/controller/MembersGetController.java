package com.mogakko.be_final.domain.members.controller;

import com.mogakko.be_final.domain.members.service.MembersGetService;
import com.mogakko.be_final.userDetails.UserDetailsImpl;
import com.mogakko.be_final.util.Message;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
@Tag(name = "회원 관련 GET 요청 API", description = "회원 관련 GET 요청 API 입니다.")
public class MembersGetController {

    private final MembersGetService membersGetService;

    @Operation(summary = "이메일 중복 체크 API", description = "이메일 중복 체크를 하는 메서드입니다.")
    @GetMapping("/signup/checkEmail")
    public ResponseEntity<Message> checkEmail(@RequestParam("email") String email) {
        return membersGetService.checkEmail(email);
    }

    @Operation(summary = "닉네임 중복 체크 API", description = "닉네임 중복 체크를 하는 메서드입니다.")
    @GetMapping("/signup/checkNickname")
    public ResponseEntity<Message> checkNickname(@RequestParam("nickname") String nickname) {
        return membersGetService.checkNickname(nickname);
    }

    @Operation(summary = "마이페이지 API", description = "마이페이지에서 '참여중인 모각코방', '총 참여 시간'을 보여주는 메서드입니다.")
    @GetMapping("/mypage")
    public ResponseEntity<Message> myPage(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return membersGetService.readMyPage(userDetails.getMember());
    }

    @Operation(summary = "다른 유저 프로필 조회 API", description = "다른 유저의 프로필을 조회하는 메서드입니다.")
    @GetMapping("/{memberId}")
    public ResponseEntity<Message> getMemberProfile(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long memberId) {
        return membersGetService.getMemberProfile(userDetails.getMember(), memberId);
    }

    @Operation(summary = "다른 유저 닉네임 검색 API", description = "다른 유저를 닉네임으로 검색하는 메서드입니다.")
    @GetMapping("/search/nickname")
    public ResponseEntity<Message> searchMembersByNickname(@RequestParam String nickname, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return membersGetService.searchMembersByNickname(nickname, userDetails.getMember());
    }


    @Operation(summary = "다른 유저 코드 검색 API", description = "다른 유저를 코드로 검색하는 메서드입니다.")
    @GetMapping("/search/friend-code")
    public ResponseEntity<Message> searchMemberByFriendsCode(@RequestParam String friendCode, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return membersGetService.searchMemberByFriendsCode(friendCode, userDetails.getMember());
    }

    @Operation(summary = "최고의 유저 조회 API", description = "순공 시간이 가장 높은 모각코인을 조회하는 메서드입니다.")
    @GetMapping("/best")
    public ResponseEntity<Message> readBestMembers() {
        return membersGetService.readBestMembers();
    }
}

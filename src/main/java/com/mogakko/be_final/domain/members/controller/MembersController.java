package com.mogakko.be_final.domain.members.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mogakko.be_final.domain.members.dto.LoginRequestDto;
import com.mogakko.be_final.domain.members.dto.SignupRequestDto;
import com.mogakko.be_final.domain.members.service.MailSendService;
import com.mogakko.be_final.domain.members.service.MembersService;
import com.mogakko.be_final.kakao.KakaoService;
import com.mogakko.be_final.userDetails.UserDetailsImpl;
import com.mogakko.be_final.util.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MembersController {
    private final KakaoService kakaoService;
    private final MembersService membersService;
    private final MailSendService mss;


    @PostMapping("/signup")
    public ResponseEntity<Message> signup(@Valid @RequestBody SignupRequestDto requestDto) {
        return membersService.signup(requestDto);
    }

    @PostMapping("/login")
    public ResponseEntity<Message> login(@RequestBody LoginRequestDto requestDto, HttpServletResponse httpServletResponse) {
        return membersService.login(requestDto, httpServletResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Message> logout(@AuthenticationPrincipal UserDetailsImpl userDetails, HttpServletRequest request) {
        return membersService.logout(userDetails.getMembers(), request);
    }

    @GetMapping("/signup/confirm")
    public ResponseEntity<Message> verifyEmail(@RequestParam String email, @RequestParam String authKey) {
        return membersService.verifyEmail(email, authKey);

    }

    @GetMapping("/kakaoLogin")
    public ResponseEntity<Message> kakaoLogin(@RequestParam("code") String code, HttpServletResponse response) throws JsonProcessingException {
        return kakaoService.kakaoLogin(code, response);
    }


//    /*자신이 참여 했던 방 리스트 보여주기. 참여 히스토리.*/
//    @GetMapping("/rooms/{page}/history")
//    public ResponseEntity<PrivateResponseBody> getAllHistoryRooms(@PathVariable int page,
//                                                                  @AuthenticationPrincipal UserDetailsImpl userDetails) {
//        return new ResponseUtil<>().forSuccess(mogakkoService.getAllHistoryChatRooms(page, userDetails.getMembers()));
//    }


}


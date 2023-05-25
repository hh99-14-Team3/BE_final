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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MembersController {
    private final KakaoService kakaoService;
    private final MembersService membersService;
    private final MailSendService mss;


    @PostMapping("/signup")
    public ResponseEntity<Message> signup(@Valid@RequestBody SignupRequestDto requestDto, HttpSession session){
        Boolean emailChecked = (Boolean) session.getAttribute("emailChecked");
        Boolean nicknameChecked = (Boolean) session.getAttribute("nicknameChecked");

        if (emailChecked == null || nicknameChecked == null || !emailChecked || !nicknameChecked) {
            Message message = Message.setSuccess("닉네임 중복검사 혹은 이메일 중복검사를 완료해주세요");
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }
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
    @GetMapping("/signup/checkEmail")
    public ResponseEntity<Boolean> checkEmail(@RequestParam("email") String email, HttpSession session){
        boolean isDuplicate = membersService.checkEmail(email);
        session.setAttribute("emailChecked", true);
        return ResponseEntity.ok(isDuplicate);
    }

    @GetMapping("/signup/checkNickname")
    public ResponseEntity<Boolean> checkNickname(@RequestParam("nickname") String nickname, HttpSession session ){
        boolean isDuplicate = membersService.checkNickname(nickname);
        session.setAttribute("nicknameChecked", true);
        return ResponseEntity.ok(isDuplicate);
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


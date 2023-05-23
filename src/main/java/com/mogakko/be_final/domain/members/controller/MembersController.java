package com.mogakko.be_final.domain.members.controller;

import com.mogakko.be_final.domain.members.dto.MembersRequestDto;
import com.mogakko.be_final.domain.members.dto.MembersResponseDto;
import com.mogakko.be_final.domain.members.service.MailSendService;
import com.mogakko.be_final.domain.members.service.MembersService;
import com.mogakko.be_final.userDetails.UserDetailsImpl;
import com.mogakko.be_final.util.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MembersController {
    private final MembersService membersService;
    private final MailSendService mss;


    @PostMapping("/signup")
    public ResponseEntity<Message> signup(@RequestBody MembersRequestDto requestDto){
        return membersService.signup(requestDto);
    }



    @PostMapping("/login")
    public ResponseEntity<Message> login(@RequestBody MembersRequestDto requestDto, HttpServletResponse httpServletResponse) {
        return membersService.login(requestDto, httpServletResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Message> logout(@AuthenticationPrincipal UserDetailsImpl userDetails, HttpServletRequest request) {
        return membersService.logout(userDetails.getMembers(), request);
    }

    @GetMapping("/signUpConfirm")
    public ResponseEntity<Message> verifyEmail(@RequestParam String email, @RequestParam String authKey) {
        return membersService.verifyEmail(email, authKey);
    }




}


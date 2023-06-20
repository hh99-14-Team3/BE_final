package com.mogakko.be_final.security.oauth2.controller;

import com.mogakko.be_final.security.oauth2.service.GithubLoginService;
import com.mogakko.be_final.userDetails.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

@RestController
@RequestMapping("/oauth2")
@RequiredArgsConstructor
@Tag(name = "GitHub 소셜 정보 연동 API", description = "GitHub 소셜 정보 연동 API 입니다.")
public class GithubLoginController {

    private final GithubLoginService githubLoginService;

    @Operation(summary = "소셜 인증 요청 API", description = "이 API에 요청이 도달하면 소셜 인증이 시작됩니다.")
    @GetMapping("/githubLogin")
    public ResponseEntity<Map<String, String>> startGithubLogin(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return githubLoginService.startGithubLogin(userDetails.getMember());
    }

    @Operation(summary = "callback 처리 API", description = "소셜 로그인 과정중에 자동으로 도달합니다. 인증 서버에서 code와 state를 받아와서 처리합니다")
    @GetMapping("/callback/github")
    public RedirectView handleCallback(@RequestParam("state") String state, @RequestParam("code") String code){
        githubLoginService.getUserDetailsFromGithub(state, code);
        //TODO: FE 페이지로 리다이렉트 할 예정
        return new RedirectView("/");

    }
}

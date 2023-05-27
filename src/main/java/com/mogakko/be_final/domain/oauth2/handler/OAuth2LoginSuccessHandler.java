package com.mogakko.be_final.domain.oauth2.handler;

import com.mogakko.be_final.domain.members.entity.Role;
import com.mogakko.be_final.domain.oauth2.CustomOAuth2User;
import com.mogakko.be_final.jwt.JwtUtil;
import com.mogakko.be_final.jwt.TokenDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor

public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 Login 성공!");
        try {
            CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

            // User의 Role이 GUEST일 경우 처음 요청한 회원이므로 회원가입 페이지로 리다이렉트
            if(oAuth2User.getRole() == Role.GUEST) {
                String accessToken = jwtUtil.createToken(oAuth2User.getEmail(), "Access");

                response.sendRedirect("/api/members/signup/agreement"); // 프론트의 회원가입 추가 정보 입력 폼으로 리다이렉트

                jwtUtil.setHeaderAccessToken(response, accessToken);

            } else {
                loginSuccess(response, oAuth2User); // 로그인에 성공한 경우 access, refresh 토큰 생성
            }
        } catch (Exception e) {
            throw e;
        }

    }

    private void loginSuccess(HttpServletResponse response, CustomOAuth2User oAuth2User) throws IOException {
        TokenDto token = jwtUtil.createAllToken(oAuth2User.getEmail());

        jwtUtil.setHeaderAccessToken(response, token.getAccessToken());
        jwtUtil.setHeaderRefreshToken(response, token.getRefreshToken());
    }
}

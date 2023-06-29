package com.mogakko.be_final.security.oauth2.handler;

import com.mogakko.be_final.domain.members.entity.Role;
import com.mogakko.be_final.domain.members.repository.MembersRepository;
import com.mogakko.be_final.security.jwt.JwtProvider;
import com.mogakko.be_final.security.jwt.TokenDto;
import com.mogakko.be_final.security.oauth2.util.CustomOAuth2User;
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

    private final JwtProvider jwtProvider;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 Login 성공!");
        try {
            CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
            // User의 Role이 GUEST일 경우 처음 요청한 회원이므로 회원가입 페이지로 리다이렉트
            if (oAuth2User.getRole() == Role.GUEST) guestLoginSuccess(response, oAuth2User);
            else userLoginSuccess(response, oAuth2User);
        } catch (Exception e) {
            throw e;
        }
    }

    private void guestLoginSuccess(HttpServletResponse response, CustomOAuth2User oAuth2User) throws IOException {
        TokenDto token = jwtProvider.createAllToken(oAuth2User.getEmail());

        jwtProvider.setHeaderAccessToken(response, token.getAccessToken());

        response.setStatus(HttpServletResponse.SC_OK);
        // TODO: 위치기반정보 활용 동의 페이지로 리다이렉트 할 예정입니다.
        // 해당 페이지에서 동의를 하고 프론트에서 토큰과 함께 지정된 api로 요청을 보내면 ROLE 을 USER 로 바꿔줄 계획입니다. ( api는 아직 미구현)
        response.sendRedirect("http://localhost:8080/");
    }

    private void userLoginSuccess(HttpServletResponse response, CustomOAuth2User oAuth2User) throws IOException {
        TokenDto token = jwtProvider.createAllToken(oAuth2User.getEmail());

        jwtProvider.setHeaderAccessToken(response, token.getAccessToken());
        jwtProvider.setHeaderRefreshToken(response, token.getRefreshToken());

        response.setStatus(HttpServletResponse.SC_OK);
        // TODO: 메인페이지로 리다이렉트 할 예정입니다. 이 부분에서 기존 로그인 하는 방법과 동일하게 진행 하려고 합니다.
        response.sendRedirect("http://localhost:8080/");
    }
}

package com.mogakko.be_final.domain.oauth2.handler;

import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.entity.Role;
import com.mogakko.be_final.domain.members.repository.MembersRepository;
import com.mogakko.be_final.domain.oauth2.CustomOAuth2User;
import com.mogakko.be_final.exception.CustomException;
import com.mogakko.be_final.exception.ErrorCode;
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
    private final MembersRepository membersRepository;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 Login 성공!");
        try {
            CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

            // User의 Role이 GUEST일 경우 처음 요청한 회원이므로 회원가입 페이지로 리다이렉트
            if(oAuth2User.getRole() == Role.GUEST) {
                Members member =membersRepository.findByEmail(oAuth2User.getEmail()).orElseThrow(
                        () -> new CustomException(ErrorCode.USER_NOT_FOUND)
                );
                member.changeRole(Role.USER);
                membersRepository.save(member);

                loginSuccess(response, oAuth2User);


            } else {
                loginSuccess(response, oAuth2User);
            }
        } catch (Exception e) {
            throw e;
        }

    }

    private void loginSuccess(HttpServletResponse response, CustomOAuth2User oAuth2User) throws IOException {
        TokenDto token = jwtUtil.createAllToken(oAuth2User.getEmail());

        jwtUtil.setHeaderAccessToken(response, token.getAccessToken());
        jwtUtil.setHeaderRefreshToken(response, token.getRefreshToken());

        response.sendRedirect("/api/main");
        //TODO:추후에 클라이언트랑 연결되면 위에코드 지우고 아래코드사용
        //response.setStatus(HttpServletResponse.SC_OK);
    }
}

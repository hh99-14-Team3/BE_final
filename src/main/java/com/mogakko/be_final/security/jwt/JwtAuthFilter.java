package com.mogakko.be_final.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mogakko.be_final.domain.members.repository.MembersRepository;
import com.mogakko.be_final.util.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final MembersRepository membersRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String access_token = jwtProvider.resolveToken(request, JwtProvider.ACCESS_KEY);
        String refresh_token = jwtProvider.resolveToken(request, JwtProvider.REFRESH_KEY);

        // 토큰이 존재하면 유효성 검사를 수행하고, 유효하지 않은 경우 예외 처리
        if (access_token == null) {
            filterChain.doFilter(request, response);
        } else {
            if (jwtProvider.validateToken(access_token)) {
                setAuthentication(jwtProvider.getUserInfoFromToken(access_token));
            } else if (refresh_token != null && jwtProvider.refreshTokenValid(refresh_token)) {
                //Refresh토큰으로 유저명 가져오기
                String email = jwtProvider.getUserInfoFromToken(refresh_token);
                //유저명으로 유저 정보 가져오기
//                Members member = memberRepository.findByEmail(email).get();
                //새로운 ACCESS TOKEN 발급
                String newAccessToken = jwtProvider.createToken(email, "Access");
                //Header에 ACCESS TOKEN 추가
                jwtProvider.setHeaderAccessToken(response, newAccessToken);
                setAuthentication(email);
            } else if (refresh_token == null) {
                jwtExceptionHandler(response, "AccessToken Expired.");
                return;
            } else {
                jwtExceptionHandler(response, "RefreshToken Expired.");
                return;
            }
            // 다음 필터로 요청과 응답을 전달하여 필터 체인 계속 실행
            filterChain.doFilter(request, response);
        }
    }

    public void setAuthentication(String email) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = jwtProvider.createAuthentication(email);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

    public void jwtExceptionHandler(HttpServletResponse response, String msg) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        try {
            String json = new ObjectMapper().writeValueAsString(new Message(msg, null));
            response.getWriter().write(json);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}

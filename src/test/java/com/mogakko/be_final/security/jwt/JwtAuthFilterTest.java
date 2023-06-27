package com.mogakko.be_final.security.jwt;

import com.mogakko.be_final.redis.util.RedisUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

    @Mock
    JwtProvider jwtProvider;
    @Mock
    RedisUtil redisUtil;
    @Mock
    HttpServletRequest request;
    @Mock
    HttpServletResponse response;
    @Mock
    FilterChain filterChain;
    @InjectMocks
    JwtAuthFilter jwtAuthFilter;

    @Nested
    @DisplayName("doFilterInternal Method 테스트")
    class DoFilterInternal {
        @DisplayName("doFilterInternal 성공 테스트")
        @Test
        void doFilterInternal_success() throws ServletException, IOException {
            // given
            String accessToken = "accessToken";

            when(jwtProvider.resolveToken(request, JwtProvider.ACCESS_KEY)).thenReturn(accessToken);
            when(jwtProvider.resolveToken(request, JwtProvider.REFRESH_KEY)).thenReturn(null);
            when(jwtProvider.validateToken(accessToken)).thenReturn(true);
            when(jwtProvider.getUserInfoFromToken(accessToken)).thenReturn("test@test.com");

            // when
            jwtAuthFilter.doFilterInternal(request, response, filterChain);
            // then

            verify(jwtProvider, times(1)).getUserInfoFromToken(accessToken);
        }

        @DisplayName("doFilterInternal accessToken 재발급 테스트")
        @Test
        void doFilterInternal_newAccessToken() throws ServletException, IOException {
            // given
            String accessToken = "accessToken";
            String newAccessToken = "newAccessToken";
            String refreshToken = "refreshToken";
            String newRefreshToken = "newRefreshToken";
            String email = "test@test.com";
            long tokenTime = 60L;

            when(jwtProvider.resolveToken(request, JwtProvider.ACCESS_KEY)).thenReturn(accessToken);
            when(jwtProvider.resolveToken(request, JwtProvider.REFRESH_KEY)).thenReturn(refreshToken);
            when(jwtProvider.validateToken(accessToken)).thenReturn(false);
            when(jwtProvider.refreshTokenValid(refreshToken)).thenReturn(true);
            when(jwtProvider.getUserInfoFromToken(refreshToken)).thenReturn(email);
            when(jwtProvider.createToken(email, "Access")).thenReturn(newAccessToken);
            when(jwtProvider.getExpirationTime(refreshToken)).thenReturn(tokenTime);
            when(jwtProvider.createNewRefreshToken(email, tokenTime)).thenReturn(newRefreshToken);

            // when
            jwtAuthFilter.doFilterInternal(request, response, filterChain);

            // then
            verify(redisUtil, times(1)).set(email, newRefreshToken, tokenTime);
            verify(jwtProvider, times(1)).setHeaderRefreshToken(response, newRefreshToken);
            verify(filterChain, times(1)).doFilter(request, response);
        }

        @DisplayName("doFilterInternal accessToken null 테스트")
        @Test
        void doFilterInternal_accessTokenValueIsNull() throws ServletException, IOException {
            // given
            when(jwtProvider.resolveToken(request, JwtProvider.ACCESS_KEY)).thenReturn(null);
            when(jwtProvider.resolveToken(request, JwtProvider.REFRESH_KEY)).thenReturn(null);

            // when
            jwtAuthFilter.doFilterInternal(request, response, filterChain);

            // then
            verify(filterChain, times(1)).doFilter(request, response);
        }

        @DisplayName("doFilterInternal refreshToken 유효성 검사 실패 테스트")
        @Test
        void doFilterInternal_invalidRefreshToken() throws ServletException, IOException {
            // given
            String accessToken = "accessToken";
            String refreshToken = "refreshToken";

            when(jwtProvider.resolveToken(request, JwtProvider.ACCESS_KEY)).thenReturn(accessToken);
            when(jwtProvider.resolveToken(request, JwtProvider.REFRESH_KEY)).thenReturn(refreshToken);
            when(jwtProvider.validateToken(accessToken)).thenReturn(false);
            when(jwtProvider.refreshTokenValid(refreshToken)).thenReturn(false);

            // when & then
            jwtAuthFilter.doFilterInternal(request, response, filterChain);
        }

        @DisplayName("doFilterInternal refreshToken null 실패 테스트")
        @Test
        void doFilterInternal_refreshTokenValueIsNull() throws ServletException, IOException {
            // given
            String accessToken = "accessToken";

            when(jwtProvider.resolveToken(request, JwtProvider.ACCESS_KEY)).thenReturn(accessToken);
            when(jwtProvider.resolveToken(request, JwtProvider.REFRESH_KEY)).thenReturn(null);
            when(jwtProvider.validateToken(accessToken)).thenReturn(false);

            // when & then
            jwtAuthFilter.doFilterInternal(request, response, filterChain);
        }
    }

    @Nested
    @DisplayName("JwtExceptionHandler 테스트")
    class JwtExceptionHandler {
        @DisplayName("JwtExceptionHandler 성공 테스트")
        @Test
        void jwtExceptionHandler_success() throws IOException {
            // given
            String expectedMsg = "Test Message";

            PrintWriter writer = Mockito.mock(PrintWriter.class);
            when(response.getWriter()).thenReturn(writer);

            // when
            jwtAuthFilter.jwtExceptionHandler(response, expectedMsg);

            // then
            verify(response).setStatus(Mockito.eq(HttpServletResponse.SC_UNAUTHORIZED));
            verify(response).setContentType(Mockito.eq("application/json"));
            String expectedJson = "{\"message\":\"Test Message\",\"data\":null}";
            verify(writer).write(expectedJson);
        }
    }
}
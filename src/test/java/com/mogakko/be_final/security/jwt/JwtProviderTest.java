package com.mogakko.be_final.security.jwt;

import com.mogakko.be_final.redis.util.RedisUtil;
import com.mogakko.be_final.userDetails.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;

import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtProviderTest {

    private static final String BEARER_PREFIX = "Bearer ";
    public static final String ACCESS_KEY = "ACCESS_KEY";
    public static final String REFRESH_KEY = "REFRESH_KEY";
    private static final long ACCESS_TIME = Duration.ofMinutes(30).toMillis();
    private static final long REFRESH_TIME = Duration.ofDays(7).toMillis();

    @Value("${jwt.secret.key}")
    private String secretKey;
    @Mock
    Key key;
    @Mock
    SignatureAlgorithm signatureAlgorithm;
    @Mock
    HttpServletRequest request;

    @Mock
    UserDetailsServiceImpl userDetailsService;
    @Mock
    RedisUtil redisUtil;
    @InjectMocks
    JwtProvider jwtProvider;


//    @Nested
//    @DisplayName("init 테스트")
//    class init {
//    }

    @Nested
    @DisplayName("resolveToken Method 테스트")
    class ResolveToken {
        @DisplayName("resolveToken ASSESS_KEY 성공 테스트")
        @Test
        void resolveToken_successWithASSESS_KEY() {
            // given
            String token = "ACCESS_KEY";
            String accessToken = "Bearer accessToken";

            when(request.getHeader(token)).thenReturn(accessToken);

            // when
            String resolveToken = jwtProvider.resolveToken(request, token);

            // then
            assertEquals("accessToken", resolveToken);
        }

        @DisplayName("resolveToken REFRESH_KEY 성공 테스트")
        @Test
        void resolveToken_successWithREFRESH_KEY() {
            // given
            String token = "REFRESH_KEY";
            String accessToken = "Bearer refreshToken";

            when(request.getHeader(token)).thenReturn(accessToken);

            // when
            String resolveToken = jwtProvider.resolveToken(request, token);

            // then
            assertEquals("refreshToken", resolveToken);
        }

        @DisplayName("resolveToken 실패 테스트")
        @Test
        void resolveToken_failWithInvalidToken() {
            // given
            String token = "REFRESH_KEY";
            String accessToken = "Be refreshToken";

            when(request.getHeader(token)).thenReturn(accessToken);

            // when
            String resolveToken = jwtProvider.resolveToken(request, token);

            // then
            assertNull(resolveToken);
        }

        @DisplayName("resolveToken 토큰 null 값 실패 테스트")
        @Test
        void resolveToken_failWithNullInput() {
            // given
            String token = "ACCESS_KEY";
            String accessToken = null;

            when(request.getHeader(token)).thenReturn(accessToken);

            // when
            String resolveToken = jwtProvider.resolveToken(request, token);

            // then
            assertNull(resolveToken);
        }
    }

    @Nested
    @DisplayName("socketResolveToken Method 테스트")
    class SocketResolveToken {
        @DisplayName("socketResolveToken 성공 테스트")
        @Test
        void socketResolveToken_success() {
            // given
            String token = "Bearer token";

            // when
            String socketResolveToken = jwtProvider.socketResolveToken(token);

            // then
            assertEquals("token", socketResolveToken);
        }

        @DisplayName("socketResolveToken 토큰 유효성 테스트")
        @Test
        void socketResolveToken_failWithInvalidToken() {
            // given
            String token = "Bea token";

            // when
            String socketResolveToken = jwtProvider.socketResolveToken(token);

            // then
            assertNull(socketResolveToken);
        }
        @DisplayName("socketResolveToken 토큰 유효성 테스트")
        @Test
        void socketResolveToken_failWithInputNull() {
            // given
            String token = null;

            // when
            String socketResolveToken = jwtProvider.socketResolveToken(token);

            // then
            assertNull(socketResolveToken);
        }
    }

    @Test
    void createAllToken() {
    }

    @Test
    void createToken() {
    }

    @Test
    void createNewRefreshToken() {
    }

    @Test
    void validateToken() {
    }

    @Test
    void getUserInfoFromToken() {
    }

    @Test
    void createAuthentication() {
    }

    @Test
    void refreshTokenValid() {
    }

    @Test
    void setHeaderAccessToken() {
    }

    @Test
    void setHeaderRefreshToken() {
    }

    @Test
    void getExpirationTime() {
    }
}
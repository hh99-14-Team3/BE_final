package com.mogakko.be_final.security.jwt;

import com.mogakko.be_final.redis.util.RedisUtil;
import com.mogakko.be_final.userDetails.UserDetailsServiceImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Key;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {
    private final RedisUtil redisUtil;
    private static final String BEARER_PREFIX = "Bearer ";
    public static final String ACCESS_KEY = "ACCESS_KEY";
    public static final String REFRESH_KEY = "REFRESH_KEY";
    private static final long ACCESS_TIME = Duration.ofMinutes(60).toMillis();
    private static final long REFRESH_TIME = Duration.ofDays(7).toMillis();

    @Value("${jwt.secret.key}")
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
    private final UserDetailsServiceImpl userDetailsService;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // header 토큰을 가져오기
    public String resolveToken(HttpServletRequest request, String token) {
        String tokenName = token.equals("ACCESS_KEY") ? ACCESS_KEY : REFRESH_KEY;
        String bearerToken = request.getHeader(tokenName);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // Socket Access 토큰의 유효성 검증
    public String socketResolveToken(String bearerToken) {
        log.info("resolve socket Token ...");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            // Bearer- 제외 토큰 값만 리턴
            return bearerToken.substring(7);
        }

        // 토큰이 없다면 null 리턴
        return null;
    }

    // 토큰 생성
    public TokenDto createAllToken(String email) {
        return new TokenDto(createToken(email, "Access"), createToken(email, "Refresh"));
    }

    public String createToken(String email, String token) {
        Date date = new Date();
        long tokenType = token.equals("Access") ? ACCESS_TIME : REFRESH_TIME;

        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(email)
                        .setExpiration(new Date(date.getTime() + tokenType))
                        .setIssuedAt(date)
                        .signWith(key, signatureAlgorithm)
                        .compact();
    }

    //Refresh Token 재발급
    public String createNewRefreshToken(String memberId, long time) {
        Date date = new Date();

        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(memberId)
                        .setExpiration(new Date(date.getTime() + time))
                        .setIssuedAt(date)
                        .signWith(key, signatureAlgorithm)
                        .compact();
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        if (redisUtil.hasKeyBlackList(token))
            return false;
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token, 만료된 JWT token 입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
        return false;
    }

    // 토큰에서 사용자 정보 가져오기
    public String getUserInfoFromToken(String token) {
        Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
        return claims.getBody().getSubject();
    }

    // 인증 객체 생성
    public Authentication createAuthentication(String email) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    //RefreshToken 검증
    public boolean refreshTokenValid(String token) {
        if (!validateToken(token)) return false;
        String refreshToken = null;
        Object redisValue = redisUtil.get(getUserInfoFromToken(token));
        if (redisValue != null) {
            refreshToken = redisValue.toString().substring(7);
        } else {
            return false;
        }
        return refreshToken.equals(token) && !refreshToken.isEmpty();
    }

    public void setHeaderAccessToken(HttpServletResponse response, String accessToken) {
        response.setHeader(ACCESS_KEY, accessToken);
    }

    public void setHeaderRefreshToken(HttpServletResponse response, String refreshToken) {
        response.setHeader(REFRESH_KEY, refreshToken);
    }

    // 토큰에서 만료 시간 정보 추출
    public long getExpirationTime(String token) {
        Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
        // 현재 시간과 만료 시간의 차이를 계산하여 반환
        Date expirationDate = claims.getBody().getExpiration();
        Date now = new Date();
        return (expirationDate.getTime() - now.getTime());
    }
}
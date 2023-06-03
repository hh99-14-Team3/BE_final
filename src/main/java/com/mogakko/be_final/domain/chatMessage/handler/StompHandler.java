package com.mogakko.be_final.domain.chatMessage.handler;

import com.mogakko.be_final.exception.CustomException;
import com.mogakko.be_final.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import static com.mogakko.be_final.exception.ErrorCode.AUTHENTICATION_FAILED;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {
    private final JwtProvider jwtProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (accessor.getCommand() == StompCommand.CONNECT) {
            log.info("소켓 Connect JWT 확인");
            String accessToken = accessor.getFirstNativeHeader("ACCESS_KEY");
            String refreshToken = accessor.getFirstNativeHeader("REFRESH_KEY");

            String jwtAccessToken = jwtProvider.socketResolveToken(accessToken);

            if (jwtAccessToken != null && jwtProvider.validateToken(jwtAccessToken)) {
                log.info("엑세스 토큰 인증 성공");
            } else if (jwtAccessToken != null && !jwtProvider.validateToken(jwtAccessToken)) {
                log.info("JWT 토큰이 만료되어, Refresh token 확인 작업을 진행합니다.");
                String jwtRefreshToken = jwtProvider.socketResolveToken(refreshToken);
                if (jwtRefreshToken != null && jwtProvider.validateToken(jwtRefreshToken)) {
                    log.info("리프레시 토큰 인증 성공");
                } else {
                    throw new CustomException(AUTHENTICATION_FAILED);
                }
            } else {
                throw new CustomException(AUTHENTICATION_FAILED);
            }
        }
        return message;
    }
}

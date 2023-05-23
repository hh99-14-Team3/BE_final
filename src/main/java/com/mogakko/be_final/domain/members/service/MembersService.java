package com.mogakko.be_final.domain.members.service;

import com.mogakko.be_final.domain.members.dto.MembersRequestDto;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.repository.MembersRepository;
import com.mogakko.be_final.exception.CustomException;
import com.mogakko.be_final.exception.ErrorCode;
import com.mogakko.be_final.jwt.JwtUtil;
import com.mogakko.be_final.jwt.TokenDto;
import com.mogakko.be_final.jwt.refreshToken.RefreshToken;
import com.mogakko.be_final.jwt.refreshToken.RefreshTokenRepository;
import com.mogakko.be_final.redis.util.RedisUtil;
import com.mogakko.be_final.util.Message;
import com.mogakko.be_final.util.StatusEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static com.mogakko.be_final.exception.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MembersService {

    private final MembersRepository membersRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    public ResponseEntity<Message> signup(MembersRequestDto membersRequestDto){
        String email = membersRequestDto.getEmail();
        String password = passwordEncoder.encode(membersRequestDto.getPassword());
        String nickname = membersRequestDto.getNickname();

        Optional<Members> findMembersByEmail = membersRepository.findByEmail(email);
        if (findMembersByEmail.isPresent()) {
            log.info("중복된 이메일 입니다.");
            throw new CustomException(DUPLICATE_IDENTIFIER);
        }

        Optional<Members> findMembersByNickName = membersRepository.findByNickName(nickname);
        if (findMembersByNickName.isPresent()) {
            log.info("중복된 닉네임 입니다.");
            throw new CustomException(DUPLICATE_IDENTIFIER);
        }


        Members members = new Members(email, password, nickname, true, true);
        membersRepository.save(members);
        Message message = Message.setSuccess(StatusEnum.OK, "회원가입 성공");
        return new ResponseEntity<>(message, HttpStatus.OK);

    }

    //서비스 자체로그인
    public ResponseEntity<Message> login(MembersRequestDto membersRequestDto, HttpServletResponse httpServletResponse){
        Members members = membersRepository.findByEmail(membersRequestDto.getEmail()).orElseThrow(
                () -> new CustomException(USER_NOT_FOUND)
        );

        if (!passwordEncoder.matches(membersRequestDto.getPassword(), members.getPassword())){
            throw new CustomException(INVALID_PASSWORD);
        }

        TokenDto tokenDto = jwtUtil.createAllToken(members.getEmail());

        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByEmail(membersRequestDto.getEmail());
        if (refreshToken.isPresent()) {
            refreshToken.get().updateToken(tokenDto.getRefreshToken());
        } else { // 존재하지 않을 경우 새로 발급
            RefreshToken newToken = new RefreshToken(tokenDto.getRefreshToken(), members.getEmail());
            refreshTokenRepository.save(newToken);
        }

        httpServletResponse.addHeader(JwtUtil.ACCESS_KEY, tokenDto.getAccessToken());
//        httpServletResponse.addHeader(JwtUtil.REFRESH_KEY, tokenDto.getRefreshToken());

        Message message = Message.setSuccess(StatusEnum.OK, "로그인 성공");
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    // 로그아웃
    public ResponseEntity<Message> logout(Members members, HttpServletRequest request) {
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByEmail(members.getEmail());
        String accessToken = request.getHeader("ACCESS_KEY").substring(7);
        if(refreshToken.isPresent()) {
            Long tokenTime = jwtUtil.getExpirationTime(accessToken);
            redisUtil.setBlackList(accessToken, "access_token", tokenTime);
            refreshTokenRepository.deleteByEmail(members.getEmail());
            Message message = Message.setSuccess(StatusEnum.OK, "로그아웃 성공", members.getEmail());
            return new ResponseEntity<>(message, HttpStatus.OK);
        }
        throw new CustomException(USER_NOT_FOUND);

    }



}

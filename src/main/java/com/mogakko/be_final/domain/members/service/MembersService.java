package com.mogakko.be_final.domain.members.service;

import com.mogakko.be_final.domain.members.dto.LoginRequestDto;
import com.mogakko.be_final.domain.members.dto.SignupRequestDto;
import com.mogakko.be_final.domain.members.entity.EmailVerification;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.repository.EmailVerificationRepository;
import com.mogakko.be_final.domain.members.repository.MembersRepository;
import com.mogakko.be_final.exception.CustomException;
import com.mogakko.be_final.jwt.JwtUtil;
import com.mogakko.be_final.jwt.TokenDto;
import com.mogakko.be_final.jwt.refreshToken.RefreshToken;
import com.mogakko.be_final.jwt.refreshToken.RefreshTokenRepository;
import com.mogakko.be_final.redis.util.RedisUtil;
import com.mogakko.be_final.util.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
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
    private final EmailVerificationRepository emailVerificationRepository;
    private final MailSendService mailSendService;


    @Transactional
    public ResponseEntity<Message> signup(SignupRequestDto signupRequestDto){
        String email = signupRequestDto.getEmail();
        String password = passwordEncoder.encode(signupRequestDto.getPassword());
        String nickname = signupRequestDto.getNickname();

        Optional<Members> findMembersByEmail = membersRepository.findByEmail(email);
        if (findMembersByEmail.isPresent()) {
            log.info("중복된 이메일 입니다.");
            throw new CustomException(DUPLICATE_IDENTIFIER);
        }

        Optional<Members> findMembersByNickname = membersRepository.findByNickname(nickname);
        if (findMembersByNickname.isPresent()) {
            log.info("중복된 닉네임 입니다.");
            throw new CustomException(DUPLICATE_IDENTIFIER);
        }
        boolean agree = Boolean.parseBoolean(signupRequestDto.getIsAgreed());

        if(!agree){
            log.info("필수 항목에 동의해 주세요.");
            throw new CustomException(IS_NOT_AGREED);

        }

        Members members = new Members(email, nickname, password, true, false);
        membersRepository.save(members);

        mailSendService.sendAuthMail(email);

        Message message = Message.setSuccess("이메일 인증을 완료해 주세요.", null);
        return new ResponseEntity<>(message, HttpStatus.OK);

    }

    //서비스 자체로그인
    public ResponseEntity<Message> login(LoginRequestDto loginRequestDto, HttpServletResponse httpServletResponse){
        Members members = membersRepository.findByEmail(loginRequestDto.getEmail()).orElseThrow(
                () -> new CustomException(USER_NOT_FOUND)
        );
        if (!members.isEmailAuth()) {
            throw new CustomException(NOT_VERIFIED_EMAIL);
        }

        if (!passwordEncoder.matches(loginRequestDto.getPassword(), members.getPassword())){
            throw new CustomException(INVALID_PASSWORD);
        }

        TokenDto tokenDto = jwtUtil.createAllToken(members.getEmail());

        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByEmail(loginRequestDto.getEmail());
        if (refreshToken.isPresent()) {
            refreshToken.get().updateToken(tokenDto.getRefreshToken());
        } else { // 존재하지 않을 경우 새로 발급
            RefreshToken newToken = new RefreshToken(tokenDto.getRefreshToken(), members.getEmail());
            refreshTokenRepository.save(newToken);
        }

        httpServletResponse.addHeader(JwtUtil.ACCESS_KEY, tokenDto.getAccessToken());
//        httpServletResponse.addHeader(JwtUtil.REFRESH_KEY, tokenDto.getRefreshToken());

        Message message = Message.setSuccess("로그인 성공",null);
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
            Message message = Message.setSuccess("로그아웃 성공", members.getEmail());
            return new ResponseEntity<>(message, HttpStatus.OK);
        }
        throw new CustomException(USER_NOT_FOUND);

    }


    public ResponseEntity<Message> verifyEmail(String email, String authKey) {
        // find the user by email
        Optional<Members> findMemberByEmail = membersRepository.findByEmail(email);
        if (!findMemberByEmail.isPresent()) {
            log.error("User not found with email: " + email);
            throw new CustomException(USER_NOT_FOUND);
        }

        // find the auth key record by email
        Optional<EmailVerification> emailVerificationRecord = emailVerificationRepository.findByEmail(email);
        if (!emailVerificationRecord.isPresent() || !emailVerificationRecord.get().getVerificationKey().equals(authKey)) {
            log.error("Invalid auth key for email: " + email);
            throw new CustomException(INVALID_AUTH_KEY);
        }

        // check if the auth key has expired
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(emailVerificationRecord.get().getExpirationTime())) {
            throw new CustomException(EXPIRED_AUTH_KEY);
        }

        Members member = findMemberByEmail.get();
        member.emailVerifiedSuccess();
        membersRepository.save(member);

        emailVerificationRepository.delete(emailVerificationRecord.get());

        Message message = Message.setSuccess("이메일 인증이 완료되었습니다.", null);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }



}

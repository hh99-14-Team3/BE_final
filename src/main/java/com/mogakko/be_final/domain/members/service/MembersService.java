package com.mogakko.be_final.domain.members.service;

import com.mogakko.be_final.domain.members.dto.request.LoginRequestDto;
import com.mogakko.be_final.domain.members.dto.request.SignupRequestDto;
import com.mogakko.be_final.domain.members.dto.response.MyPageResponseDto;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.entity.Role;
import com.mogakko.be_final.domain.members.repository.MembersRepository;
import com.mogakko.be_final.domain.mogakkoRoom.entity.MogakkoRoomMembers;
import com.mogakko.be_final.domain.mogakkoRoom.entity.MogakkoRoomTime;
import com.mogakko.be_final.domain.mogakkoRoom.repository.MogakkoRoomMembersRepository;
import com.mogakko.be_final.domain.mogakkoRoom.repository.MogakkoRoomRepository;
import com.mogakko.be_final.domain.mogakkoRoom.repository.MogakkoRoomTimeRepository;
import com.mogakko.be_final.domain.sse.service.NotificationService;
import com.mogakko.be_final.exception.CustomException;
import com.mogakko.be_final.redis.util.RedisUtil;
import com.mogakko.be_final.security.jwt.JwtUtil;
import com.mogakko.be_final.security.jwt.TokenDto;
import com.mogakko.be_final.security.refreshToken.RefreshToken;
import com.mogakko.be_final.security.refreshToken.RefreshTokenRepository;
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
import javax.servlet.http.HttpSession;
import java.sql.Time;
import java.util.List;
import java.util.Optional;

import static com.mogakko.be_final.exception.ErrorCode.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MembersService {

    private final MembersRepository membersRepository;
    private final MogakkoRoomMembersRepository mogakkoRoomMembersRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final NotificationService notificationService;
    private final MogakkoRoomRepository mogakkoRoomRepository;
    private final MogakkoRoomTimeRepository mogakkoRoomTimeRepository;


    // 회원가입
        public ResponseEntity<Message> signup(SignupRequestDto signupRequestDto, HttpSession session) {
        String email = signupRequestDto.getEmail();
        String password = passwordEncoder.encode(signupRequestDto.getPassword());
        String nickname = signupRequestDto.getNickname();
//        boolean isAgreed = Boolean.parseBoolean(signupRequestDto.getIsAgreed());
//
//        if (!isAgreed) {
//            log.info("필수 항목에 동의해 주세요.");
//            throw new CustomException(IS_NOT_AGREED);
//        }
        Boolean emailChecked = (Boolean) session.getAttribute("emailChecked");
        Boolean nicknameChecked = (Boolean) session.getAttribute("nicknameChecked");

        if (nicknameChecked == null || !nicknameChecked || emailChecked == null || !emailChecked) {
            return new ResponseEntity<>(new Message("이메일과 닉네임 중복검사를 완료해주세요", null), HttpStatus.BAD_REQUEST);
        }

        Members members = new Members(email, nickname, password, Role.USER);
        membersRepository.save(members);
        MogakkoRoomTime mogakkoRoomTimes = new MogakkoRoomTime(email, Time.valueOf("00:00:00"));
        mogakkoRoomTimeRepository.save(mogakkoRoomTimes);
        return new ResponseEntity<>(new Message("회원 가입 성공", null), HttpStatus.OK);

    }

    @Transactional(readOnly = true)
    public ResponseEntity<Message> checkEmail(String email) {
        if (membersRepository.findByEmail(email).isPresent()) {
            log.info("중복된 이메일 입니다.");
            throw new CustomException(DUPLICATE_IDENTIFIER);
        }
        return new ResponseEntity<>(new Message("중복 확인 성공", null), HttpStatus.OK);

    }

    @Transactional(readOnly = true)
    public ResponseEntity<Message> checkNickname(String nickname) {
        if (membersRepository.findByNickname(nickname).isPresent()) {
            log.info("중복된 닉네임 입니다.");
            throw new CustomException(DUPLICATE_NICKNAME);
        }
        return new ResponseEntity<>(new Message("중복 확인 성공", null), HttpStatus.OK);
    }

    // 로그인
    public ResponseEntity<Message> login(LoginRequestDto loginRequestDto, HttpServletResponse httpServletResponse) {
        Members members = membersRepository.findByEmail(loginRequestDto.getEmail()).orElseThrow(
                () -> new CustomException(USER_NOT_FOUND)
        );

        if (!passwordEncoder.matches(loginRequestDto.getPassword(), members.getPassword())) {
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

        notificationService.sendLoginNotification(members);

        httpServletResponse.addHeader(JwtUtil.ACCESS_KEY, tokenDto.getAccessToken());
//        httpServletResponse.addHeader(JwtUtil.REFRESH_KEY, tokenDto.getRefreshToken());


        Message message = Message.setSuccess("로그인 성공", null);

        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    // 로그아웃
    public ResponseEntity<Message> logout(Members members, HttpServletRequest request) {
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByEmail(members.getEmail());
        String accessToken = request.getHeader("ACCESS_KEY").substring(7);
        if (refreshToken.isPresent()) {
            Long tokenTime = jwtUtil.getExpirationTime(accessToken);
            redisUtil.setBlackList(accessToken, "access_token", tokenTime);
            refreshTokenRepository.deleteByEmail(members.getEmail());
            Message message = Message.setSuccess("로그아웃 성공", members.getEmail());
            return new ResponseEntity<>(message, HttpStatus.OK);
        }
        throw new CustomException(USER_NOT_FOUND);
    }


    // 마이페이지 조회 - 내가 참여중인 모각코 방, 총 참여 시간
    @Transactional(readOnly = true)
    public ResponseEntity<Message> readMyPage(Members member) {
        List<MogakkoRoomMembers> mogakkoRoomList = mogakkoRoomMembersRepository.findAllByMemberIdAndMogakkoRoomIsDeletedFalse(member.getId());
        MogakkoRoomTime mogakkoTotalTime = mogakkoRoomTimeRepository.findByMember(member.getEmail());
        MyPageResponseDto myPageResponseDto = new MyPageResponseDto(mogakkoRoomList, mogakkoTotalTime, member);

        return new ResponseEntity<>(new Message("마이페이지 조회 성공", myPageResponseDto), HttpStatus.OK);
    }
}

package com.mogakko.be_final.domain.members.service;

import com.mogakko.be_final.S3.S3Uploader;
import com.mogakko.be_final.domain.members.dto.request.LoginRequestDto;
import com.mogakko.be_final.domain.members.dto.request.SignupRequestDto;
import com.mogakko.be_final.domain.members.dto.response.MyPageResponseDto;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.entity.Role;
import com.mogakko.be_final.domain.members.repository.MembersRepository;
import com.mogakko.be_final.domain.mogakkoRoom.entity.MogakkoRoomMembers;
import com.mogakko.be_final.domain.mogakkoRoom.entity.MogakkoRoomTime;
import com.mogakko.be_final.domain.mogakkoRoom.repository.MogakkoRoomMembersRepository;
import com.mogakko.be_final.domain.mogakkoRoom.repository.MogakkoRoomTimeRepository;
import com.mogakko.be_final.domain.sse.service.NotificationSendService;
import com.mogakko.be_final.exception.CustomException;
import com.mogakko.be_final.redis.util.RedisUtil;
import com.mogakko.be_final.security.jwt.JwtProvider;
import com.mogakko.be_final.security.jwt.TokenDto;
import com.mogakko.be_final.util.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Time;
import java.time.Duration;
import java.util.List;

import static com.mogakko.be_final.exception.ErrorCode.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MembersService {

    private final MembersRepository membersRepository;
    private final MogakkoRoomMembersRepository mogakkoRoomMembersRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RedisUtil redisUtil;
    private final NotificationSendService notificationSendService;
    private final MogakkoRoomTimeRepository mogakkoRoomTimeRepository;
    private final RedisTemplate redisTemplate;
    private final S3Uploader s3Uploader;


    // 회원가입
    public ResponseEntity<Message> signup(SignupRequestDto signupRequestDto, HttpSession session) {
        String email = signupRequestDto.getEmail();
        String password = passwordEncoder.encode(signupRequestDto.getPassword());
        String nickname = signupRequestDto.getNickname();

        Boolean emailChecked = (Boolean) session.getAttribute("emailChecked");
        Boolean nicknameChecked = (Boolean) session.getAttribute("nicknameChecked");
        String checkedEmail = (String) session.getAttribute("email");
        String checkedNickname = (String) session.getAttribute("nickname");

        if (nicknameChecked == null || !nicknameChecked || emailChecked == null || !emailChecked) {
            return new ResponseEntity<>(new Message("이메일과 닉네임 중복검사를 완료해주세요", null), HttpStatus.BAD_REQUEST);
        }
        if (!checkedEmail.equals(signupRequestDto.getEmail()) || !checkedNickname.equals(signupRequestDto.getNickname())) {
            return new ResponseEntity<>(new Message("중복검사에 사용한 이메일과 닉네임을 사용해 주세요", null), HttpStatus.BAD_REQUEST);
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
        Members member = membersRepository.findByEmail(loginRequestDto.getEmail()).orElseThrow(
                () -> new CustomException(USER_NOT_FOUND)
        );

        if (!passwordEncoder.matches(loginRequestDto.getPassword(), member.getPassword())) {
            throw new CustomException(INVALID_PASSWORD);
        }

        TokenDto tokenDto = jwtProvider.createAllToken(member.getEmail());
        String refreshToken = tokenDto.getRefreshToken();
        redisUtil.set(member.getEmail(), refreshToken, Duration.ofDays(7).toMillis());
        httpServletResponse.addHeader(JwtProvider.ACCESS_KEY, tokenDto.getAccessToken());

        Message message = Message.setSuccess("로그인 성공", member.getNickname());
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    // 로그아웃
    public ResponseEntity<Message> logout(Members member, HttpServletRequest request) {
        String accessToken = request.getHeader("ACCESS_KEY").substring(7);
        String refreshToken = redisUtil.get(member.getEmail());
        if (!refreshToken.isEmpty()) {
            long tokenTime = jwtProvider.getExpirationTime(accessToken);
            redisUtil.setBlackList(accessToken, "access_token", tokenTime);
            redisUtil.delValues(member.getEmail());
            return new ResponseEntity<>(new Message("로그아웃 성공", member.getEmail()), HttpStatus.OK);
        }
        throw new CustomException(USER_NOT_FOUND);
    }


    // 마이페이지 조회 - 내가 참여중인 모각코 방, 총 참여 시간
    @Transactional(readOnly = true)
    public ResponseEntity<Message> readMyPage(Members member) {
        List<MogakkoRoomMembers> mogakkoRoomList = mogakkoRoomMembersRepository.findAllByMemberIdAndMogakkoRoomIsDeletedFalse(member.getId());
        Time mogakkoTotalTime = mogakkoRoomTimeRepository.findMogakkoRoomTimeByEmail(member.getEmail());
        MyPageResponseDto myPageResponseDto = new MyPageResponseDto(mogakkoRoomList, mogakkoTotalTime, member);

        return new ResponseEntity<>(new Message("마이페이지 조회 성공", myPageResponseDto), HttpStatus.OK);
    }

    // 마이페이지 - 프로필 사진, 닉네임 수정
    public ResponseEntity<Message> profileUpdate(MultipartFile imageFile, String nickname, Members member) throws IOException {
        if (imageFile != null && !imageFile.isEmpty()) {
            s3Uploader.delete(member.getProfileImage());
            String profileImage = s3Uploader.uploadFile(imageFile);
            member.updateProfileImage(profileImage);
        }

        if (nickname != null && !nickname.isEmpty()) {
            member.updateNickname(nickname);
        }

        membersRepository.save(member);
        return new ResponseEntity<>(new Message("프로필 정보 변경 성공", null), HttpStatus.OK);
    }

    // 마이페이지 - 프로필 사진 삭제
    public ResponseEntity<Message> profileDelete(Members member) {
        member.deleteProfile();
        return new ResponseEntity<>(new Message("프로필 사진 삭제 성공", null), HttpStatus.OK);
    }

}

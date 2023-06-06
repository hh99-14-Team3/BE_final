package com.mogakko.be_final.domain.members.service;

import com.mogakko.be_final.S3.S3Uploader;
import com.mogakko.be_final.domain.members.dto.request.GithubIdRequestDto;
import com.mogakko.be_final.domain.members.dto.request.LoginRequestDto;
import com.mogakko.be_final.domain.members.dto.request.SignupRequestDto;
import com.mogakko.be_final.domain.members.dto.response.MyPageResponseDto;
import com.mogakko.be_final.domain.members.dto.response.UserPageResponseDto;
import com.mogakko.be_final.domain.members.entity.MemberStatusCode;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.entity.Role;
import com.mogakko.be_final.domain.members.repository.MembersRepository;
import com.mogakko.be_final.domain.mogakkoRoom.dto.response.MogakkoTimerResponseDto;
import com.mogakko.be_final.domain.mogakkoRoom.entity.MogakkoRoomMembers;
import com.mogakko.be_final.domain.mogakkoRoom.entity.MogakkoRoomTime;
import com.mogakko.be_final.domain.mogakkoRoom.repository.MogakkoRoomMembersRepository;
import com.mogakko.be_final.domain.mogakkoRoom.repository.MogakkoRoomTimeRepository;
import com.mogakko.be_final.domain.mogakkoRoom.repository.MogakkoTimerRepository;
import com.mogakko.be_final.domain.mogakkoRoom.service.MogakkoService;
import com.mogakko.be_final.exception.CustomException;
import com.mogakko.be_final.redis.util.RedisUtil;
import com.mogakko.be_final.security.jwt.JwtProvider;
import com.mogakko.be_final.security.jwt.TokenDto;
import com.mogakko.be_final.util.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Time;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static com.mogakko.be_final.exception.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MembersService {

    private final MembersRepository membersRepository;
    private final MogakkoRoomMembersRepository mogakkoRoomMembersRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RedisUtil redisUtil;
    private final MogakkoRoomTimeRepository mogakkoRoomTimeRepository;
    private final S3Uploader s3Uploader;
    private final MogakkoService mogakkoService;
    private final MogakkoTimerRepository mogakkoTimerRepository;


    // 회원가입
    @Transactional
    public ResponseEntity<Message> signup(SignupRequestDto signupRequestDto) {
        String email = signupRequestDto.getEmail();
        String password = passwordEncoder.encode(signupRequestDto.getPassword());
        String nickname = signupRequestDto.getNickname();

        if (membersRepository.findByEmail(email).isPresent()) {
            throw new CustomException(ALREADY_JOIN_USER);
        }

        Members members = new Members(email, nickname, password, Role.USER, MemberStatusCode.BASIC);
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
    @Transactional
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

        Message message = Message.setSuccess("로그인 성공", member);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    // 로그아웃
    @Transactional
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


    // 마이페이지 조회 - 내가 참여중인 모각코 방, 총 참여 시간, 매너 ON:도 등 개인 정보
    @Transactional(readOnly = true)
    public ResponseEntity<Message> readMyPage(Members member) {
        List<MogakkoRoomMembers> mogakkoRoomList = mogakkoRoomMembersRepository.findAllByMemberIdAndMogakkoRoomIsDeletedFalse(member.getId());
        Time mogakkoTotalTime = mogakkoRoomTimeRepository.findMogakkoRoomTimeByEmail(member.getEmail());
        String nickname = member.getNickname();
        List<Long> mogakkoTotalTimer = mogakkoTimerRepository.findAllByNicknameAndMogakkoTimer(nickname);
        List<Long> mogakkoTotalTimerWeek = mogakkoTimerRepository.findAllByNicknameAndMogakkoTimer(nickname, LocalDateTime.now().minusDays(7));
        Long totalTime = 0L;
        String mogakkoTimes = changeSecToTime(totalTime, mogakkoTotalTimer, member);
        String mogakkoTimesWeek = changeSecToTime(totalTime, mogakkoTotalTimerWeek, member);

        MogakkoTimerResponseDto mogakkoTime = new MogakkoTimerResponseDto(mogakkoTimes, mogakkoTimesWeek);
        MyPageResponseDto myPageResponseDto = new MyPageResponseDto(mogakkoRoomList, mogakkoTotalTime, member, mogakkoTime);
        return new ResponseEntity<>(new Message("마이페이지 조회 성공", myPageResponseDto), HttpStatus.OK);
    }

    // 마이페이지 - 프로필 사진, 닉네임 수정
    @Transactional
    public ResponseEntity<Message> profileUpdate(MultipartFile imageFile, String nickname, Members member) throws IOException {
        if (member.getMemberStatusCode().equals(MemberStatusCode.BASIC)) {
            member.changeMemberStatusCode(MemberStatusCode.NORMAL);
        }

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

    // 다른 유저 프로필 조회
    @Transactional(readOnly = true)
    public ResponseEntity<Message> getMemberProfile(Long memberId) {
        Members member = membersRepository.findById(memberId).orElseThrow(
                () -> new CustomException(USER_NOT_FOUND)
        );
        Time mogakkoTotalTime = mogakkoRoomTimeRepository.findMogakkoRoomTimeByEmail(member.getEmail());
        UserPageResponseDto userPageResponseDto = new UserPageResponseDto(member, mogakkoTotalTime);
        return new ResponseEntity<>(new Message("프로필 조회 성공", userPageResponseDto), HttpStatus.OK);
    }

    // 깃허브 아이디 등록
    @Transactional
    public ResponseEntity<Message> addGithub(GithubIdRequestDto githubIdRequestDto, Members member) {
        String githubId = githubIdRequestDto.getGithubId();
        if (membersRepository.findByGithubId(githubId).isPresent()) {
            log.info("중복된 깃허브 아이디 입니다.");
            throw new CustomException(DUPLICATE_IDENTIFIER);
        }
        member.setGithubId(githubId);
        return new ResponseEntity<>(new Message("깃허브 아이디 등록 성공", githubId), HttpStatus.OK);
    }

    /**
     * Method
     */
    public String changeSecToTime(Long totalTime, List<Long> mogakkoTotalTimer, Members member) {
        synchronized (totalTime) {
            if(totalTime >= 4140 && totalTime < 14886) member.changeMemberStatusCode(MemberStatusCode.SPECIAL_DOG);
            if(totalTime >= 14886 && totalTime < 36240) member.changeMemberStatusCode(MemberStatusCode.SPECIAL_LOVE);
            if(totalTime >= 36240 && totalTime < 90840) member.changeMemberStatusCode(MemberStatusCode.SPECIAL_ANGEL);
            if(totalTime >= 90840) member.changeMemberStatusCode(MemberStatusCode.SPECIAL_LOVELOVE);
            membersRepository.save(member);

            for (int i = 0; i < mogakkoTotalTimer.size(); i++) {
                totalTime = totalTime + mogakkoTotalTimer.get(i);
            }

            Long hour, min, sec;

            sec = totalTime % 60;
            min = totalTime / 60 % 60;
            hour = totalTime / 3600;

            String timerBuffer = String.format("%02d:%02d:%02d", hour, min, sec);
            return timerBuffer;
        }
    }
}

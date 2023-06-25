package com.mogakko.be_final.domain.members.service;

import com.mogakko.be_final.domain.friendship.repository.FriendshipRepository;
import com.mogakko.be_final.domain.members.dto.request.GithubIdRequestDto;
import com.mogakko.be_final.domain.members.dto.request.LoginRequestDto;
import com.mogakko.be_final.domain.members.dto.request.SignupRequestDto;
import com.mogakko.be_final.domain.members.dto.response.MemberResponseDto;
import com.mogakko.be_final.domain.members.entity.MemberStatusCode;
import com.mogakko.be_final.domain.members.entity.MemberWeekStatistics;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.entity.Role;
import com.mogakko.be_final.domain.members.repository.MemberWeekStatisticsRepository;
import com.mogakko.be_final.domain.members.repository.MembersRepository;
import com.mogakko.be_final.exception.CustomException;
import com.mogakko.be_final.redis.util.RedisUtil;
import com.mogakko.be_final.security.jwt.JwtProvider;
import com.mogakko.be_final.security.jwt.TokenDto;
import com.mogakko.be_final.util.BadWordFiltering;
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
import java.time.Duration;

import static com.mogakko.be_final.exception.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MembersPostService {
    private final JwtProvider jwtProvider;
    private final RedisUtil redisUtil;
    private final BadWordFiltering badWordFiltering;
    private final PasswordEncoder passwordEncoder;
    private final FriendshipRepository friendshipRepository;
    private final MembersRepository membersRepository;
    private final MemberWeekStatisticsRepository memberWeekStatisticsRepository;

    // 회원가입
    @Transactional
    public ResponseEntity<Message> signup(SignupRequestDto signupRequestDto) {
        String email = signupRequestDto.getEmail();
        String password = passwordEncoder.encode(signupRequestDto.getPassword());
        String nickname = badWordFiltering.checkBadWordUUID(signupRequestDto.getNickname());

        if (membersRepository.findByEmail(email).isPresent()) {
            throw new CustomException(ALREADY_JOIN_USER);
        }

        int friendCode;
        do {
            friendCode = (int) ((Math.random() * ((999999 - 100000) + 1)) + 100000);
        } while (membersRepository.existsByFriendCode(friendCode));
        Members member = Members.builder()
                .email(email)
                .nickname(nickname)
                .password(password)
                .role(Role.USER)
                .codingTem(36.5)
                .mogakkoTotalTime(0L)
                .memberStatusCode(MemberStatusCode.BASIC)
                .profileImage("https://source.boringavatars.com/beam/120/$" + nickname + "?colors=00F0FF,172435,394254,EAEBED,F9F9FA")
                .friendCode(friendCode)
                .declared(0)
                .build();

        membersRepository.save(member);

        // 회원가입 시 주간 통계 기본 설정
        memberWeekStatisticsRepository.save(MemberWeekStatistics.builder().email(member.getEmail()).build());
        return new ResponseEntity<>(new Message("회원 가입 성공", null), HttpStatus.OK);
    }

    // 로그인
    @Transactional
    public ResponseEntity<Message> login(LoginRequestDto loginRequestDto, HttpServletResponse httpServletResponse) {
        String email = loginRequestDto.getEmail();
        Members member = membersRepository.findByEmail(email).orElseThrow(
                () -> new CustomException(USER_NOT_FOUND)
        );

        //대소문자 구별하기 위한 코드
        if (!member.getEmail().equals(email)) throw new CustomException(INVALID_EMAIL);

        if (!passwordEncoder.matches(loginRequestDto.getPassword(), member.getPassword())) {
            throw new CustomException(INVALID_PASSWORD);
        }

        TokenDto tokenDto = jwtProvider.createAllToken(member.getEmail());
        String refreshToken = tokenDto.getRefreshToken();
        redisUtil.set(member.getEmail(), refreshToken, Duration.ofDays(7).toMillis());
        httpServletResponse.addHeader(JwtProvider.ACCESS_KEY, tokenDto.getAccessToken());
        httpServletResponse.addHeader(JwtProvider.REFRESH_KEY, tokenDto.getRefreshToken());
        String nickname = member.getNickname();
        String profileImage = member.getProfileImage();
        boolean isTutorialCheck = member.isTutorialCheck();
        Role role = member.getRole();

        return new ResponseEntity<>(new Message("로그인 성공", new MemberResponseDto(nickname, profileImage, isTutorialCheck, role)), HttpStatus.OK);
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

    // 깃허브 아이디 등록
    @Transactional
    public ResponseEntity<Message> addGithub(GithubIdRequestDto githubIdRequestDto, Members member) {
        String githubId = githubIdRequestDto.getGithubId();
        if (membersRepository.findByGithubId(githubId).isPresent()) {
            log.info("중복된 깃허브 아이디 입니다.");
            throw new CustomException(DUPLICATE_GITHUB_ID);
        }
        member.setGithubId(githubId);

        // TODO : OSIV 설정으로 필요한 코드임 (리팩토링 후 삭제 예정)
        membersRepository.save(member);
        return new ResponseEntity<>(new Message("깃허브 아이디 등록 성공", githubId), HttpStatus.OK);
    }
}

package com.mogakko.be_final.domain.mogakkoRoom.service;

import com.mogakko.be_final.domain.members.entity.*;
import com.mogakko.be_final.domain.members.repository.MemberWeekStatisticsRepository;
import com.mogakko.be_final.domain.members.repository.MembersRepository;
import com.mogakko.be_final.domain.mogakkoRoom.dto.request.MogakkoTimerRequestDto;
import com.mogakko.be_final.exception.CustomException;
import com.mogakko.be_final.util.Message;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.Time;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Optional;

import static com.mogakko.be_final.exception.ErrorCode.INTERNAL_SERER_ERROR;
import static com.mogakko.be_final.exception.ErrorCode.USER_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MogakkoPutServiceTest {

    @Mock
    MembersRepository membersRepository;
    @Mock
    MemberWeekStatisticsRepository memberWeekStatisticsRepository;
    @Mock
    LocalDateTime localDateTime;
    @InjectMocks
    MogakkoPutService mogakkoPutService;

    Members member = Members.builder()
            .id(1L)
            .email("test@example.com")
            .nickname("nickname")
            .memberStatusCode(MemberStatusCode.BASIC)
            .mogakkoTotalTime(0L)
            .githubId("github")
            .profileImage("image")
            .role(Role.USER)
            .socialUid("id")
            .socialType(SocialType.GOOGLE)
            .password("1q2w3e4r")
            .codingTem(36.5)
            .build();

    @Nested
    @DisplayName("모각코 타이머 테스트")
    class mogakkoTimer {
        @DisplayName("모각코 타이머 성공 테스트")
        @Test
        void mogakkoTimer_success() {
            // given
            MogakkoTimerRequestDto mogakkoTimerRequestDto = MogakkoTimerRequestDto.builder()
                    .mogakkoTimer("00:20:34")
                    .build();

            MemberWeekStatistics memberWeekStatistics = MemberWeekStatistics.builder()
                    .email("test@example.com")
                    .sun(0).mon(0).tue(0).sat(0).fri(0).wed(0).thu(0)
                    .build();

            when(memberWeekStatisticsRepository.findById(member.getEmail())).thenReturn(Optional.of(memberWeekStatistics));

            // when
            ResponseEntity<Message> response = mogakkoPutService.mogakkoTimer(mogakkoTimerRequestDto, member);

            // then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("저장 성공", response.getBody().getMessage());
            assertEquals(Time.valueOf("00:20:34"), response.getBody().getData());
        }

        @DisplayName("모각코 타이머 24시간 초과 테스트")
        @Test
        void mogakkoTimer_overFlowTime() {
            // given
            MogakkoTimerRequestDto mogakkoTimerRequestDto = MogakkoTimerRequestDto.builder()
                    .mogakkoTimer("24:20:34")
                    .build();

            MemberWeekStatistics memberWeekStatistics = MemberWeekStatistics.builder()
                    .email("test@example.com")
                    .sun(0).mon(0).tue(0).sat(0).fri(0).wed(0).thu(0)
                    .build();

            when(memberWeekStatisticsRepository.findById(member.getEmail())).thenReturn(Optional.of(memberWeekStatistics));

            // when
            ResponseEntity<Message> response = mogakkoPutService.mogakkoTimer(mogakkoTimerRequestDto, member);

            // then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("저장 성공", response.getBody().getMessage());
            assertEquals(Time.valueOf("20:00:00"), response.getBody().getData());
        }

        @DisplayName("없는 모각코 타이머 테스트")
        @Test
        void mogakkoTimer_notFoundMembersWeekTimer() {
            // given
            MogakkoTimerRequestDto mogakkoTimerRequestDto = MogakkoTimerRequestDto.builder()
                    .mogakkoTimer("24:20:34")
                    .build();

            when(memberWeekStatisticsRepository.findById(member.getEmail())).thenReturn(Optional.empty());

            // when & then
            CustomException customException = assertThrows(CustomException.class, ()-> mogakkoPutService.mogakkoTimer(mogakkoTimerRequestDto, member));
            assertEquals(USER_NOT_FOUND, customException.getErrorCode());
        }

        @DisplayName("모각코 타이머 멤버 스테이터스 코드 전환 (EMOTICON) 테스트")
        @Test
        void mogakkoTimer_EMOTICON() {
            // given
            Members member = Members.builder()
                    .id(1L)
                    .email("test@example.com")
                    .nickname("nickname")
                    .memberStatusCode(MemberStatusCode.BASIC)
                    .mogakkoTotalTime(3810000L)
                    .githubId("github")
                    .profileImage("image")
                    .role(Role.USER)
                    .socialUid("id")
                    .socialType(SocialType.GOOGLE)
                    .password("1q2w3e4r")
                    .codingTem(100.0)
                    .build();

            MogakkoTimerRequestDto mogakkoTimerRequestDto = MogakkoTimerRequestDto.builder()
                    .mogakkoTimer("24:20:34")
                    .build();

            MemberWeekStatistics memberWeekStatistics = MemberWeekStatistics.builder()
                    .email("test@example.com")
                    .sun(0).mon(0).tue(0).sat(0).fri(0).wed(0).thu(0)
                    .build();

            when(memberWeekStatisticsRepository.findById(member.getEmail())).thenReturn(Optional.of(memberWeekStatistics));

            // when
            ResponseEntity<Message> response = mogakkoPutService.mogakkoTimer(mogakkoTimerRequestDto, member);

            // then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("저장 성공", response.getBody().getMessage());
            assertEquals(Time.valueOf("20:00:00"), response.getBody().getData());
            assertEquals(member.getCodingTem(), 100.0);
            assertEquals(member.getMemberStatusCode(), MemberStatusCode.EMOTICON);
        }@DisplayName("모각코 타이머 멤버 스테이터스 코드 전환 (SPECIAL_ANGEL) 테스트")
        @Test
        void mogakkoTimer_SPECIAL_ANGEL() {
            // given
            Members member = Members.builder()
                    .id(1L)
                    .email("test@example.com")
                    .nickname("nickname")
                    .memberStatusCode(MemberStatusCode.BASIC)
                    .mogakkoTotalTime(36250L)
                    .githubId("github")
                    .profileImage("image")
                    .role(Role.USER)
                    .socialUid("id")
                    .socialType(SocialType.GOOGLE)
                    .password("1q2w3e4r")
                    .codingTem(36.5)
                    .build();

            MogakkoTimerRequestDto mogakkoTimerRequestDto = MogakkoTimerRequestDto.builder()
                    .mogakkoTimer("00:20:34")
                    .build();

            MemberWeekStatistics memberWeekStatistics = MemberWeekStatistics.builder()
                    .email("test@example.com")
                    .sun(0).mon(0).tue(0).sat(0).fri(0).wed(0).thu(0)
                    .build();

            when(memberWeekStatisticsRepository.findById(member.getEmail())).thenReturn(Optional.of(memberWeekStatistics));

            // when
            ResponseEntity<Message> response = mogakkoPutService.mogakkoTimer(mogakkoTimerRequestDto, member);

            // then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("저장 성공", response.getBody().getMessage());
            assertEquals(Time.valueOf("00:20:34"), response.getBody().getData());
            assertEquals(member.getCodingTem(), 37.14);
            assertEquals(member.getMemberStatusCode(), MemberStatusCode.SPECIAL_ANGEL);
        }

        @DisplayName("모각코 타이머 멤버 스테이터스 코드 전환 (SPECIAL_LOVE) 테스트")
        @Test
        void mogakkoTimer_SPECIAL_LOVE() {
            // given
            Members member = Members.builder()
                    .id(1L)
                    .email("test@example.com")
                    .nickname("nickname")
                    .memberStatusCode(MemberStatusCode.BASIC)
                    .mogakkoTotalTime(14886L)
                    .githubId("github")
                    .profileImage("image")
                    .role(Role.USER)
                    .socialUid("id")
                    .socialType(SocialType.GOOGLE)
                    .password("1q2w3e4r")
                    .codingTem(100.0)
                    .build();

            MogakkoTimerRequestDto mogakkoTimerRequestDto = MogakkoTimerRequestDto.builder()
                    .mogakkoTimer("00:20:34")
                    .build();

            MemberWeekStatistics memberWeekStatistics = MemberWeekStatistics.builder()
                    .email("test@example.com")
                    .sun(0).mon(0).tue(0).sat(0).fri(0).wed(0).thu(0)
                    .build();

            when(memberWeekStatisticsRepository.findById(member.getEmail())).thenReturn(Optional.of(memberWeekStatistics));

            // when
            ResponseEntity<Message> response = mogakkoPutService.mogakkoTimer(mogakkoTimerRequestDto, member);

            // then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("저장 성공", response.getBody().getMessage());
            assertEquals(Time.valueOf("00:20:34"), response.getBody().getData());
            assertEquals(member.getCodingTem(), 36.78);
            assertEquals(member.getMemberStatusCode(), MemberStatusCode.SPECIAL_LOVE);
        }

        @DisplayName("모각코 타이머 멤버 스테이터스 코드 전환 (SPECIAL_DOG) 테스트")
        @Test
        void mogakkoTimer_SPECIAL_DOG() {
            // given
            Members member = Members.builder()
                    .id(1L)
                    .email("test@example.com")
                    .nickname("nickname")
                    .memberStatusCode(MemberStatusCode.BASIC)
                    .mogakkoTotalTime(4000L)
                    .githubId("github")
                    .profileImage("image")
                    .role(Role.USER)
                    .socialUid("id")
                    .socialType(SocialType.GOOGLE)
                    .password("1q2w3e4r")
                    .codingTem(100.0)
                    .build();

            MogakkoTimerRequestDto mogakkoTimerRequestDto = MogakkoTimerRequestDto.builder()
                    .mogakkoTimer("00:20:34")
                    .build();

            MemberWeekStatistics memberWeekStatistics = MemberWeekStatistics.builder()
                    .email("test@example.com")
                    .sun(0).mon(0).tue(0).sat(0).fri(0).wed(0).thu(0)
                    .build();

            when(memberWeekStatisticsRepository.findById(member.getEmail())).thenReturn(Optional.of(memberWeekStatistics));

            // when
            ResponseEntity<Message> response = mogakkoPutService.mogakkoTimer(mogakkoTimerRequestDto, member);

            // then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("저장 성공", response.getBody().getMessage());
            assertEquals(Time.valueOf("00:20:34"), response.getBody().getData());
            assertEquals(member.getCodingTem(), 36.6);
            assertEquals(member.getMemberStatusCode(), MemberStatusCode.SPECIAL_DOG);
        }

        @DisplayName("모각코 타이머 멤버 코딩 온도 100도 초과 테스트")
        @Test
        void mogakkoTimer_overCodingTem() {
            // given
            Members member = Members.builder()
                    .id(1L)
                    .email("test@example.com")
                    .nickname("nickname")
                    .memberStatusCode(MemberStatusCode.BASIC)
                    .mogakkoTotalTime(3810000L)
                    .githubId("github")
                    .profileImage("image")
                    .role(Role.USER)
                    .socialUid("id")
                    .socialType(SocialType.GOOGLE)
                    .password("1q2w3e4r")
                    .codingTem(110.0)
                    .build();

            MogakkoTimerRequestDto mogakkoTimerRequestDto = MogakkoTimerRequestDto.builder()
                    .mogakkoTimer("24:20:34")
                    .build();

            MemberWeekStatistics memberWeekStatistics = MemberWeekStatistics.builder()
                    .email("test@example.com")
                    .sun(0).mon(0).tue(0).sat(0).fri(0).wed(0).thu(0)
                    .build();

            when(memberWeekStatisticsRepository.findById(member.getEmail())).thenReturn(Optional.of(memberWeekStatistics));

            // when
            ResponseEntity<Message> response = mogakkoPutService.mogakkoTimer(mogakkoTimerRequestDto, member);

            // then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("저장 성공", response.getBody().getMessage());
            assertEquals(Time.valueOf("20:00:00"), response.getBody().getData());
            assertEquals(member.getCodingTem(), 100.0);
            assertEquals(member.getMemberStatusCode(), MemberStatusCode.EMOTICON);
        }
    }
}

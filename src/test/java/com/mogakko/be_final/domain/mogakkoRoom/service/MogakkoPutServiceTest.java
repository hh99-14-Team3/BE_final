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
import java.time.LocalDateTime;
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
            CustomException customException = assertThrows(CustomException.class, () -> mogakkoPutService.mogakkoTimer(mogakkoTimerRequestDto, member));
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
        }

        @DisplayName("모각코 타이머 멤버 스테이터스 코드 전환 (SPECIAL_ANGEL) 테스트")
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


        @Nested
        @DisplayName("요일별 타이머 기록 메서드 테스트")
        class AddTimeToWeek {
            @DisplayName("월요일 타이머 기록 테스트")
            @Test
            void addTimeToWeek_mon() {
                // given
                int mon = 1;
                long timeToSec = 30L;

                MemberWeekStatistics memberWeekStatistics = MemberWeekStatistics.builder()
                        .email("test@example.com")
                        .sun(0).mon(0).tue(0).sat(0).fri(0).wed(0).thu(0)
                        .build();
                // when
                mogakkoPutService.addTimeToWeek(mon, timeToSec, memberWeekStatistics);

                // then
                assertEquals(memberWeekStatistics.getMon(), 30L);
                assertEquals(memberWeekStatistics.getTue(), 0L);
                assertEquals(memberWeekStatistics.getWed(), 0L);
                assertEquals(memberWeekStatistics.getThu(), 0L);
                assertEquals(memberWeekStatistics.getFri(), 0L);
                assertEquals(memberWeekStatistics.getSat(), 0L);
                assertEquals(memberWeekStatistics.getSun(), 0L);
            }

            @DisplayName("화요일 타이머 기록 테스트")
            @Test
            void addTimeToWeek_Tue() {
                // given
                int tue = 2;
                long timeToSec = 30L;

                MemberWeekStatistics memberWeekStatistics = MemberWeekStatistics.builder()
                        .email("test@example.com")
                        .sun(0).mon(0).tue(0).sat(0).fri(0).wed(0).thu(0)
                        .build();
                // when
                mogakkoPutService.addTimeToWeek(tue, timeToSec, memberWeekStatistics);

                // then
                assertEquals(memberWeekStatistics.getMon(), 0L);
                assertEquals(memberWeekStatistics.getTue(), 30L);
                assertEquals(memberWeekStatistics.getWed(), 0L);
                assertEquals(memberWeekStatistics.getThu(), 0L);
                assertEquals(memberWeekStatistics.getFri(), 0L);
                assertEquals(memberWeekStatistics.getSat(), 0L);
                assertEquals(memberWeekStatistics.getSun(), 0L);
            }

            @DisplayName("수요일 타이머 기록 테스트")
            @Test
            void addTimeToWeek_wed() {
                // given
                int wed = 3;
                long timeToSec = 30L;

                MemberWeekStatistics memberWeekStatistics = MemberWeekStatistics.builder()
                        .email("test@example.com")
                        .sun(0).mon(0).tue(0).sat(0).fri(0).wed(0).thu(0)
                        .build();
                // when
                mogakkoPutService.addTimeToWeek(wed, timeToSec, memberWeekStatistics);

                // then
                assertEquals(memberWeekStatistics.getMon(), 0L);
                assertEquals(memberWeekStatistics.getTue(), 0L);
                assertEquals(memberWeekStatistics.getWed(), 30L);
                assertEquals(memberWeekStatistics.getThu(), 0L);
                assertEquals(memberWeekStatistics.getFri(), 0L);
                assertEquals(memberWeekStatistics.getSat(), 0L);
                assertEquals(memberWeekStatistics.getSun(), 0L);
            }

            @DisplayName("목요일 타이머 기록 테스트")
            @Test
            void addTimeToWeek_thu() {
                // given
                int thu = 4;
                long timeToSec = 30L;

                MemberWeekStatistics memberWeekStatistics = MemberWeekStatistics.builder()
                        .email("test@example.com")
                        .sun(0).mon(0).tue(0).sat(0).fri(0).wed(0).thu(0)
                        .build();
                // when
                mogakkoPutService.addTimeToWeek(thu, timeToSec, memberWeekStatistics);

                // then
                assertEquals(memberWeekStatistics.getMon(), 0L);
                assertEquals(memberWeekStatistics.getTue(), 0L);
                assertEquals(memberWeekStatistics.getWed(), 0L);
                assertEquals(memberWeekStatistics.getThu(), 30L);
                assertEquals(memberWeekStatistics.getFri(), 0L);
                assertEquals(memberWeekStatistics.getSat(), 0L);
                assertEquals(memberWeekStatistics.getSun(), 0L);
            }

            @DisplayName("금요일 타이머 기록 테스트")
            @Test
            void addTimeToWeek_fri() {
                // given
                int fri = 5;
                long timeToSec = 30L;

                MemberWeekStatistics memberWeekStatistics = MemberWeekStatistics.builder()
                        .email("test@example.com")
                        .sun(0).mon(0).tue(0).sat(0).fri(0).wed(0).thu(0)
                        .build();
                // when
                mogakkoPutService.addTimeToWeek(fri, timeToSec, memberWeekStatistics);

                // then
                assertEquals(memberWeekStatistics.getMon(), 0L);
                assertEquals(memberWeekStatistics.getTue(), 0L);
                assertEquals(memberWeekStatistics.getWed(), 0L);
                assertEquals(memberWeekStatistics.getThu(), 0L);
                assertEquals(memberWeekStatistics.getFri(), 30L);
                assertEquals(memberWeekStatistics.getSat(), 0L);
                assertEquals(memberWeekStatistics.getSun(), 0L);
            }

            @DisplayName("토요일 타이머 기록 테스트")
            @Test
            void addTimeToWeek_sat() {
                // given
                int sat = 6;
                long timeToSec = 30L;

                MemberWeekStatistics memberWeekStatistics = MemberWeekStatistics.builder()
                        .email("test@example.com")
                        .sun(0).mon(0).tue(0).sat(0).fri(0).wed(0).thu(0)
                        .build();
                // when
                mogakkoPutService.addTimeToWeek(sat, timeToSec, memberWeekStatistics);

                // then
                assertEquals(memberWeekStatistics.getMon(), 0L);
                assertEquals(memberWeekStatistics.getTue(), 0L);
                assertEquals(memberWeekStatistics.getWed(), 0L);
                assertEquals(memberWeekStatistics.getThu(), 0L);
                assertEquals(memberWeekStatistics.getFri(), 0L);
                assertEquals(memberWeekStatistics.getSat(), 30L);
                assertEquals(memberWeekStatistics.getSun(), 0L);
            }

            @DisplayName("일요일 타이머 기록 테스트")
            @Test
            void addTimeToWeek_sun() {
                // given
                int sun = 7;
                long timeToSec = 30L;

                MemberWeekStatistics memberWeekStatistics = MemberWeekStatistics.builder()
                        .email("test@example.com")
                        .sun(0).mon(0).tue(0).sat(0).fri(0).wed(0).thu(0)
                        .build();
                // when
                mogakkoPutService.addTimeToWeek(sun, timeToSec, memberWeekStatistics);

                // then
                assertEquals(memberWeekStatistics.getMon(), 0L);
                assertEquals(memberWeekStatistics.getTue(), 0L);
                assertEquals(memberWeekStatistics.getWed(), 0L);
                assertEquals(memberWeekStatistics.getThu(), 0L);
                assertEquals(memberWeekStatistics.getFri(), 0L);
                assertEquals(memberWeekStatistics.getSat(), 0L);
                assertEquals(memberWeekStatistics.getSun(), 30L);
            }

            @DisplayName("요일별 타이머 기록 예외 테스트")
            @Test
            void addTimeToWeek_fail() {
                // given
                int num = 11;
                long timeToSec = 30L;

                MemberWeekStatistics memberWeekStatistics = MemberWeekStatistics.builder()
                        .email("test@example.com")
                        .sun(0).mon(0).tue(0).sat(0).fri(0).wed(0).thu(0)
                        .build();

                // when & then
                IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () -> mogakkoPutService.addTimeToWeek(num, timeToSec, memberWeekStatistics));
                assertEquals(String.valueOf(INTERNAL_SERER_ERROR), illegalArgumentException.getMessage());
            }
        }
    }
}

package com.mogakko.be_final.domain.members.util;

import com.mogakko.be_final.domain.members.entity.MemberWeekStatistics;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.repository.MemberWeekStatisticsRepository;
import com.mogakko.be_final.domain.members.repository.MembersRepository;
import com.mogakko.be_final.exception.CustomException;
import com.mogakko.be_final.util.TimeUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static com.mogakko.be_final.exception.ErrorCode.INTERNAL_SERER_ERROR;
import static com.mogakko.be_final.exception.ErrorCode.USER_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;


@ExtendWith({MockitoExtension.class})
class MembersServiceUtilMethodTest {

    @Mock
    MembersRepository membersRepository;
    @Mock
    MemberWeekStatisticsRepository memberWeekStatisticsRepository;
    @InjectMocks
    MembersServiceUtilMethod membersServiceUtilMethod;

    MemberWeekStatistics memberWeekStatistics = MemberWeekStatistics.builder()
            .email("test@example.com")
            .sun(0).mon(0).tue(0).sat(0).fri(0).wed(0).thu(0)
            .build();

    @Nested
    @DisplayName("findMemberWeekStatistics Method 테스트")
    class FindMemberWeekStatistics {
        @DisplayName("findMemberWeekStatistics 성공 테스트")
        @Test
        void findMemberWeekStatistics_success() {
            // given
            String email = "test@test.com";
            MemberWeekStatistics memberWeekStatistics = MemberWeekStatistics.builder().email(email).build();

            when(memberWeekStatisticsRepository.findById(email)).thenReturn(Optional.of(memberWeekStatistics));

            // when
            MemberWeekStatistics response = membersServiceUtilMethod.findMemberWeekStatistics(email);

            // then
            assertEquals(memberWeekStatistics.getEmail(), response.getEmail());
        }

        @DisplayName("findMemberWeekStatistics 예외 테스트")
        @Test
        void findMemberWeekStatistics_fail() {
            // given
            String email = "test@test.com";

            when(memberWeekStatisticsRepository.findById(email)).thenReturn(Optional.empty());

            // when & then
            CustomException customException = assertThrows(CustomException.class, () -> membersServiceUtilMethod.findMemberWeekStatistics(email));
            assertEquals(USER_NOT_FOUND, customException.getErrorCode());
        }
    }

    @Nested
    @DisplayName("findMemberByNickname Method 테스트")
    class FindMemberByNickname {
        @DisplayName("findMemberByNickname 성공 테스트")
        @Test
        void findMemberByNickname_success() {
            // given
            String nickname = "nickname";
            Members member = Members.builder().nickname(nickname).build();

            when(membersRepository.findByNickname(nickname)).thenReturn(Optional.of(member));

            // when
            Members response = membersServiceUtilMethod.findMemberByNickname(nickname);

            // then
            assertEquals(member.getNickname(), response.getNickname());
        }

        @DisplayName("findMemberByNickname 예외 테스트")
        @Test
        void findMemberWeekStatistics_fail() {
            // given
            String nickname = "nickname";

            when(membersRepository.findByNickname(nickname)).thenReturn(Optional.empty());

            // when & then
            CustomException customException = assertThrows(CustomException.class, () -> membersServiceUtilMethod.findMemberByNickname(nickname));
            assertEquals(USER_NOT_FOUND, customException.getErrorCode());
        }
    }

    @Nested
    @DisplayName("findMemberByFriendCode Method 테스트")
    class FindMemberByFriendCode {
        @DisplayName("findMemberByFriendCode 성공 테스트")
        @Test
        void findMemberByNickname_success() {
            // given
            int friendCode = 123456;
            Members member = Members.builder().friendCode(123456).build();

            when(membersRepository.findByFriendCode(friendCode)).thenReturn(Optional.of(member));

            // when
            Members response = membersServiceUtilMethod.findMemberByFriendCode(friendCode);

            // then
            assertEquals(member.getFriendCode(), response.getFriendCode());
        }

        @DisplayName("findMemberByFriendCode 예외 테스트")
        @Test
        void findMemberWeekStatistics_fail() {
            // given
            int friendCode = 123456;

            when(membersRepository.findByFriendCode(friendCode)).thenReturn(Optional.empty());

            // when & then
            CustomException customException = assertThrows(CustomException.class, () -> membersServiceUtilMethod.findMemberByFriendCode(friendCode));
            assertEquals(USER_NOT_FOUND, customException.getErrorCode());
        }
    }

    @Nested
    @DisplayName("weekTimeParse Method 테스트")
    class WeekTimeParse {
        @DisplayName("weekTimeParse 월요일 테스트")
        @Test
        void weekTimeParse_mon() {
            // given
            String email = "test@test.com";
            int mon = 1;

            when(memberWeekStatisticsRepository.findById(email)).thenReturn(Optional.of(memberWeekStatistics));

            // when
            Map<String, String> response = membersServiceUtilMethod.weekTimeParse(email, mon);

            // then
            assertEquals(response.get("today"), TimeUtil.changeSecToTime(memberWeekStatistics.getMon()));
            assertEquals(response.get("Sunday"), String.valueOf(TimeUtil.changeSecToMin(memberWeekStatistics.getSun())));
            assertEquals(response.get("Monday"), String.valueOf(TimeUtil.changeSecToMin(memberWeekStatistics.getMon())));
            assertEquals(response.get("Tuesday"), String.valueOf(TimeUtil.changeSecToMin(memberWeekStatistics.getTue())));
            assertEquals(response.get("Wednesday"), String.valueOf(TimeUtil.changeSecToMin(memberWeekStatistics.getWed())));
            assertEquals(response.get("Thursday"), String.valueOf(TimeUtil.changeSecToMin(memberWeekStatistics.getThu())));
            assertEquals(response.get("Friday"), String.valueOf(TimeUtil.changeSecToMin(memberWeekStatistics.getFri())));
            assertEquals(response.get("weekTotal"), TimeUtil.changeSecToTime(memberWeekStatistics.getWeekTotalTime()));
        }

        @DisplayName("weekTimeParse 화요일 테스트")
        @Test
        void weekTimeParse_Tue() {
            // given
            String email = "test@test.com";
            int tue = 2;

            when(memberWeekStatisticsRepository.findById(email)).thenReturn(Optional.of(memberWeekStatistics));

            // when
            Map<String, String> response = membersServiceUtilMethod.weekTimeParse(email, tue);

            // then
            assertEquals(response.get("today"), TimeUtil.changeSecToTime(memberWeekStatistics.getTue()));
            assertEquals(response.get("Sunday"), String.valueOf(TimeUtil.changeSecToMin(memberWeekStatistics.getSun())));
            assertEquals(response.get("Monday"), String.valueOf(TimeUtil.changeSecToMin(memberWeekStatistics.getMon())));
            assertEquals(response.get("Tuesday"), String.valueOf(TimeUtil.changeSecToMin(memberWeekStatistics.getTue())));
            assertEquals(response.get("Wednesday"), String.valueOf(TimeUtil.changeSecToMin(memberWeekStatistics.getWed())));
            assertEquals(response.get("Thursday"), String.valueOf(TimeUtil.changeSecToMin(memberWeekStatistics.getThu())));
            assertEquals(response.get("Friday"), String.valueOf(TimeUtil.changeSecToMin(memberWeekStatistics.getFri())));
            assertEquals(response.get("weekTotal"), TimeUtil.changeSecToTime(memberWeekStatistics.getWeekTotalTime()));
        }

        @DisplayName("weekTimeParse 수요일 테스트")
        @Test
        void weekTimeParse_wed() {
            // given
            String email = "test@test.com";
            int wed = 3;

            when(memberWeekStatisticsRepository.findById(email)).thenReturn(Optional.of(memberWeekStatistics));

            // when
            Map<String, String> response = membersServiceUtilMethod.weekTimeParse(email, wed);

            // then
            assertEquals(response.get("today"), TimeUtil.changeSecToTime(memberWeekStatistics.getWed()));
            assertEquals(response.get("Sunday"), String.valueOf(TimeUtil.changeSecToMin(memberWeekStatistics.getSun())));
            assertEquals(response.get("Monday"), String.valueOf(TimeUtil.changeSecToMin(memberWeekStatistics.getMon())));
            assertEquals(response.get("Tuesday"), String.valueOf(TimeUtil.changeSecToMin(memberWeekStatistics.getTue())));
            assertEquals(response.get("Wednesday"), String.valueOf(TimeUtil.changeSecToMin(memberWeekStatistics.getWed())));
            assertEquals(response.get("Thursday"), String.valueOf(TimeUtil.changeSecToMin(memberWeekStatistics.getThu())));
            assertEquals(response.get("Friday"), String.valueOf(TimeUtil.changeSecToMin(memberWeekStatistics.getFri())));
            assertEquals(response.get("weekTotal"), TimeUtil.changeSecToTime(memberWeekStatistics.getWeekTotalTime()));
        }

        @DisplayName("weekTimeParse 목요일 테스트")
        @Test
        void weekTimeParse_the() {
            // given
            String email = "test@test.com";
            int the = 4;

            when(memberWeekStatisticsRepository.findById(email)).thenReturn(Optional.of(memberWeekStatistics));

            // when
            Map<String, String> response = membersServiceUtilMethod.weekTimeParse(email, the);

            // then
            assertEquals(response.get("today"), TimeUtil.changeSecToTime(memberWeekStatistics.getThu()));
            assertEquals(response.get("Sunday"), String.valueOf(TimeUtil.changeSecToMin(memberWeekStatistics.getSun())));
            assertEquals(response.get("Monday"), String.valueOf(TimeUtil.changeSecToMin(memberWeekStatistics.getMon())));
            assertEquals(response.get("Tuesday"), String.valueOf(TimeUtil.changeSecToMin(memberWeekStatistics.getTue())));
            assertEquals(response.get("Wednesday"), String.valueOf(TimeUtil.changeSecToMin(memberWeekStatistics.getWed())));
            assertEquals(response.get("Thursday"), String.valueOf(TimeUtil.changeSecToMin(memberWeekStatistics.getThu())));
            assertEquals(response.get("Friday"), String.valueOf(TimeUtil.changeSecToMin(memberWeekStatistics.getFri())));
            assertEquals(response.get("weekTotal"), TimeUtil.changeSecToTime(memberWeekStatistics.getWeekTotalTime()));
        }

        @DisplayName("weekTimeParse 금요일 테스트")
        @Test
        void weekTimeParse_fri() {
            // given
            String email = "test@test.com";
            int fri = 5;

            when(memberWeekStatisticsRepository.findById(email)).thenReturn(Optional.of(memberWeekStatistics));

            // when
            Map<String, String> response = membersServiceUtilMethod.weekTimeParse(email, fri);

            // then
            assertEquals(response.get("today"), TimeUtil.changeSecToTime(memberWeekStatistics.getFri()));
            assertEquals(response.get("Sunday"), String.valueOf(TimeUtil.changeSecToMin(memberWeekStatistics.getSun())));
            assertEquals(response.get("Monday"), String.valueOf(TimeUtil.changeSecToMin(memberWeekStatistics.getMon())));
            assertEquals(response.get("Tuesday"), String.valueOf(TimeUtil.changeSecToMin(memberWeekStatistics.getTue())));
            assertEquals(response.get("Wednesday"), String.valueOf(TimeUtil.changeSecToMin(memberWeekStatistics.getWed())));
            assertEquals(response.get("Thursday"), String.valueOf(TimeUtil.changeSecToMin(memberWeekStatistics.getThu())));
            assertEquals(response.get("Friday"), String.valueOf(TimeUtil.changeSecToMin(memberWeekStatistics.getFri())));
            assertEquals(response.get("weekTotal"), TimeUtil.changeSecToTime(memberWeekStatistics.getWeekTotalTime()));
        }

        @DisplayName("weekTimeParse 토요일 테스트")
        @Test
        void weekTimeParse_sat() {
            // given
            String email = "test@test.com";
            int sat = 6;

            when(memberWeekStatisticsRepository.findById(email)).thenReturn(Optional.of(memberWeekStatistics));

            // when
            Map<String, String> response = membersServiceUtilMethod.weekTimeParse(email, sat);

            // then
            assertEquals(response.get("today"), TimeUtil.changeSecToTime(memberWeekStatistics.getSat()));
            assertEquals(response.get("Sunday"), String.valueOf(TimeUtil.changeSecToMin(memberWeekStatistics.getSun())));
            assertEquals(response.get("Monday"), String.valueOf(TimeUtil.changeSecToMin(memberWeekStatistics.getMon())));
            assertEquals(response.get("Tuesday"), String.valueOf(TimeUtil.changeSecToMin(memberWeekStatistics.getTue())));
            assertEquals(response.get("Wednesday"), String.valueOf(TimeUtil.changeSecToMin(memberWeekStatistics.getWed())));
            assertEquals(response.get("Thursday"), String.valueOf(TimeUtil.changeSecToMin(memberWeekStatistics.getThu())));
            assertEquals(response.get("Friday"), String.valueOf(TimeUtil.changeSecToMin(memberWeekStatistics.getFri())));
            assertEquals(response.get("weekTotal"), TimeUtil.changeSecToTime(memberWeekStatistics.getWeekTotalTime()));
        }

        @DisplayName("weekTimeParse 일요일 테스트")
        @Test
        void weekTimeParse_sun() {
            // given
            String email = "test@test.com";
            int sun = 7;

            when(memberWeekStatisticsRepository.findById(email)).thenReturn(Optional.of(memberWeekStatistics));

            // when
            Map<String, String> response = membersServiceUtilMethod.weekTimeParse(email, sun);

            // then
            assertEquals(response.get("today"), TimeUtil.changeSecToTime(memberWeekStatistics.getSun()));
            assertEquals(response.get("Sunday"), String.valueOf(TimeUtil.changeSecToMin(memberWeekStatistics.getSun())));
            assertEquals(response.get("Monday"), String.valueOf(TimeUtil.changeSecToMin(memberWeekStatistics.getMon())));
            assertEquals(response.get("Tuesday"), String.valueOf(TimeUtil.changeSecToMin(memberWeekStatistics.getTue())));
            assertEquals(response.get("Wednesday"), String.valueOf(TimeUtil.changeSecToMin(memberWeekStatistics.getWed())));
            assertEquals(response.get("Thursday"), String.valueOf(TimeUtil.changeSecToMin(memberWeekStatistics.getThu())));
            assertEquals(response.get("Friday"), String.valueOf(TimeUtil.changeSecToMin(memberWeekStatistics.getFri())));
            assertEquals(response.get("weekTotal"), TimeUtil.changeSecToTime(memberWeekStatistics.getWeekTotalTime()));
        }

        @DisplayName("weekTimeParse 예외 테스트")
        @Test
        void weekTimeParse_fail() {
            // given
            String email = "test@test.com";
            int num = 11;

            when(memberWeekStatisticsRepository.findById(email)).thenReturn(Optional.of(memberWeekStatistics));

            // when & then
            IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, ()-> membersServiceUtilMethod.weekTimeParse(email, num));
            assertEquals(String.valueOf(INTERNAL_SERER_ERROR), illegalArgumentException.getMessage());
        }
    }
}
package com.mogakko.be_final.domain.members.util;

import com.mogakko.be_final.domain.friendship.entity.FriendshipStatus;
import com.mogakko.be_final.domain.friendship.repository.FriendshipRepository;
import com.mogakko.be_final.domain.members.entity.MemberWeekStatistics;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.repository.MemberWeekStatisticsRepository;
import com.mogakko.be_final.domain.members.repository.MembersRepository;
import com.mogakko.be_final.exception.CustomException;
import com.mogakko.be_final.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.mogakko.be_final.exception.ErrorCode.INTERNAL_SERER_ERROR;
import static com.mogakko.be_final.exception.ErrorCode.USER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class MembersServiceUtilMethod {

    private final MemberWeekStatisticsRepository memberWeekStatisticsRepository;
    private final MembersRepository membersRepository;

    public MemberWeekStatistics findMemberWeekStatistics(String email) {
        MemberWeekStatistics memberWeekStatistic = memberWeekStatisticsRepository.findById(email).orElseThrow(
                () -> new CustomException(USER_NOT_FOUND)
        );
        return memberWeekStatistic;
    }

    public Members findMemberByNickname(String memberNickname) {
        return membersRepository.findByNickname(memberNickname).orElseThrow(
                () -> new CustomException(USER_NOT_FOUND)
        );
    }

    public Members findMemberByFriendCode(Integer friendCode) {
        return membersRepository.findByFriendCode(friendCode).orElseThrow(
                () -> new CustomException(USER_NOT_FOUND)
        );
    }

    public Map<String, String> weekTimeParse(String email, int dayOfWeek) {
        MemberWeekStatistics memberWeekStatistics = findMemberWeekStatistics(email);
        Map<String, String> timeOfWeek = new HashMap<>();
        switch (dayOfWeek) {
            case 1 -> timeOfWeek.put("today", TimeUtil.changeSecToTime(memberWeekStatistics.getMon()));
            case 2 -> timeOfWeek.put("today", TimeUtil.changeSecToTime(memberWeekStatistics.getTue()));
            case 3 -> timeOfWeek.put("today", TimeUtil.changeSecToTime(memberWeekStatistics.getWed()));
            case 4 -> timeOfWeek.put("today", TimeUtil.changeSecToTime(memberWeekStatistics.getThu()));
            case 5 -> timeOfWeek.put("today", TimeUtil.changeSecToTime(memberWeekStatistics.getFri()));
            case 6 -> timeOfWeek.put("today", TimeUtil.changeSecToTime(memberWeekStatistics.getSat()));
            case 7 -> timeOfWeek.put("today", TimeUtil.changeSecToTime(memberWeekStatistics.getSun()));
            default -> {
                log.error("===========weekTimeParse Method Invalid Input Error");
                throw new IllegalArgumentException(String.valueOf(INTERNAL_SERER_ERROR));
            }
        }

        timeOfWeek.put("Sunday", String.valueOf(TimeUtil.changeSecToMin(memberWeekStatistics.getSun())));
        timeOfWeek.put("Monday", String.valueOf(TimeUtil.changeSecToMin(memberWeekStatistics.getMon())));
        timeOfWeek.put("Tuesday", String.valueOf(TimeUtil.changeSecToMin(memberWeekStatistics.getTue())));
        timeOfWeek.put("Wednesday", String.valueOf(TimeUtil.changeSecToMin(memberWeekStatistics.getWed())));
        timeOfWeek.put("Thursday", String.valueOf(TimeUtil.changeSecToMin(memberWeekStatistics.getThu())));
        timeOfWeek.put("Friday", String.valueOf(TimeUtil.changeSecToMin(memberWeekStatistics.getFri())));
        timeOfWeek.put("Saturday", String.valueOf(TimeUtil.changeSecToMin(memberWeekStatistics.getSat())));
        timeOfWeek.put("weekTotal", TimeUtil.changeSecToTime(memberWeekStatistics.getWeekTotalTime()));

        return timeOfWeek;
    }
}

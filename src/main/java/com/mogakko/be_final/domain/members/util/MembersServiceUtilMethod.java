package com.mogakko.be_final.domain.members.util;

import com.mogakko.be_final.domain.friendship.entity.FriendshipStatus;
import com.mogakko.be_final.domain.friendship.repository.FriendshipRepository;
import com.mogakko.be_final.domain.members.entity.MemberWeekStatistics;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.repository.MemberWeekStatisticsRepository;
import com.mogakko.be_final.exception.CustomException;
import com.mogakko.be_final.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.mogakko.be_final.exception.ErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class MembersServiceUtilMethod {

    private final MemberWeekStatisticsRepository memberWeekStatisticsRepository;
    private final FriendshipRepository friendshipRepository;

    public MemberWeekStatistics findMemberWeekStatistics(String email) {
        MemberWeekStatistics memberWeekStatistic = memberWeekStatisticsRepository.findById(email).orElseThrow(
                () -> new CustomException(USER_NOT_FOUND)
        );
        return memberWeekStatistic;
    }

    public Map<String, String> weekTimeParse(String email) {
        MemberWeekStatistics memberWeekStatistics = findMemberWeekStatistics(email);
        Map<String, String> timeOfWeek = new HashMap<>();
        int dayOfWeek = LocalDateTime.now().getDayOfWeek().getValue();
        switch (dayOfWeek) {
            case 1 -> timeOfWeek.put("today", TimeUtil.changeSecToTime(memberWeekStatistics.getMon()));
            case 2 -> timeOfWeek.put("today", TimeUtil.changeSecToTime(memberWeekStatistics.getTue()));
            case 3 -> timeOfWeek.put("today", TimeUtil.changeSecToTime(memberWeekStatistics.getWed()));
            case 4 -> timeOfWeek.put("today", TimeUtil.changeSecToTime(memberWeekStatistics.getThu()));
            case 5 -> timeOfWeek.put("today", TimeUtil.changeSecToTime(memberWeekStatistics.getFri()));
            case 6 -> timeOfWeek.put("today", TimeUtil.changeSecToTime(memberWeekStatistics.getSat()));
            case 7 -> timeOfWeek.put("today", TimeUtil.changeSecToTime(memberWeekStatistics.getSun()));
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

    public boolean checkFriend(Members member, Members findMember) {
        boolean isFriend = false;
        if (friendshipRepository.findBySenderAndReceiverAndStatus(findMember, member, FriendshipStatus.ACCEPT).isPresent())
            isFriend = !isFriend;
        else if (friendshipRepository.findBySenderAndReceiverAndStatus(member, findMember, FriendshipStatus.ACCEPT).isPresent())
            isFriend = !isFriend;
        else if (member.getId().equals(findMember.getId())) isFriend = !isFriend;
        return isFriend;
    }

    public boolean checkFriendStatus(Members member, Members findMember) {
        boolean isPending = false;
        if (friendshipRepository.findBySenderAndReceiverAndStatus(findMember, member, FriendshipStatus.PENDING).isPresent())
            isPending = !isPending;
        else if (friendshipRepository.findBySenderAndReceiverAndStatus(member, findMember, FriendshipStatus.PENDING).isPresent())
            isPending = !isPending;
        else if (member.getId().equals(findMember.getId())) isPending = !isPending;
        return isPending;
    }
}

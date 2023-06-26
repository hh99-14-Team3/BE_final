package com.mogakko.be_final.domain.mogakkoRoom.service;

import com.mogakko.be_final.domain.members.entity.MemberStatusCode;
import com.mogakko.be_final.domain.members.entity.MemberWeekStatistics;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.repository.MemberWeekStatisticsRepository;
import com.mogakko.be_final.domain.members.repository.MembersRepository;
import com.mogakko.be_final.domain.mogakkoRoom.dto.request.MogakkoTimerRequestDto;
import com.mogakko.be_final.domain.mogakkoRoom.util.MogakkoServiceUtilMethod;
import com.mogakko.be_final.exception.CustomException;
import com.mogakko.be_final.util.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.time.LocalDateTime;

import static com.mogakko.be_final.exception.ErrorCode.INTERNAL_SERER_ERROR;
import static com.mogakko.be_final.exception.ErrorCode.USER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class MogakkoPutService {
    private final MembersRepository membersRepository;
    private final MemberWeekStatisticsRepository memberWeekStatisticsRepository;
    private final MogakkoServiceUtilMethod mogakkoServiceUtilMethod;

    // 타이머
    @Transactional
    public ResponseEntity<Message> mogakkoTimer(MogakkoTimerRequestDto mogakkoTimerRequestDto, Members member) {
        Time mogakkoTimer;
        if (Long.parseLong(mogakkoTimerRequestDto.getMogakkoTimer().substring(0, 2)) > 23)
            mogakkoTimer = new Time(20, 0, 0);
        else mogakkoTimer = Time.valueOf(mogakkoTimerRequestDto.getMogakkoTimer());

        long timeToSec = mogakkoTimer.toLocalTime().toSecondOfDay();

        MemberWeekStatistics memberWeekStatistic = memberWeekStatisticsRepository.findById(member.getEmail()).orElseThrow(
                () -> new CustomException(USER_NOT_FOUND)
        );

        addTimeToWeek(LocalDateTime.now().getDayOfWeek().getValue(), timeToSec, memberWeekStatistic);

        member.setTime(timeToSec);
        long totalTimer = member.getMogakkoTotalTime() + timeToSec;

        if (member.getCodingTem() <= 100) {
            long num = totalTimer / 600;
            Double numCnt = Math.round((num * 0.01) * 100) / 100.0;
            member.addCodingTem(numCnt);
        }

        if (totalTimer >= 4140 && totalTimer < 14886) member.changeMemberStatusCode(MemberStatusCode.SPECIAL_DOG);
        if (totalTimer >= 14886 && totalTimer < 36240) member.changeMemberStatusCode(MemberStatusCode.SPECIAL_LOVE);
        if (totalTimer >= 36240 && totalTimer < 90840) member.changeMemberStatusCode(MemberStatusCode.SPECIAL_ANGEL);
        if (totalTimer >= 90840 && totalTimer < 3810000)
            member.changeMemberStatusCode(MemberStatusCode.SPECIAL_LOVELOVE);
        if (totalTimer >= 3810000) {
            member.changeMemberStatusCode(MemberStatusCode.EMOTICON);
            member.addCodingTem(63.5);
        }

        membersRepository.save(member);
        return new ResponseEntity<>(new Message("저장 성공", mogakkoTimer), HttpStatus.OK);
    }

    @Transactional
    void addTimeToWeek(int dayOfWeek, long timeToSec, MemberWeekStatistics memberWeekStatistic) {
        switch (dayOfWeek) {
            case 1 -> memberWeekStatistic.addMon(timeToSec);
            case 2 -> memberWeekStatistic.addTue(timeToSec);
            case 3 -> memberWeekStatistic.addWed(timeToSec);
            case 4 -> memberWeekStatistic.addThu(timeToSec);
            case 5 -> memberWeekStatistic.addFri(timeToSec);
            case 6 -> memberWeekStatistic.addSat(timeToSec);
            case 7 -> memberWeekStatistic.addSun(timeToSec);
            default -> {
                log.error("===========addTimeToWeek Method Invalid Input Error");
                throw new IllegalArgumentException(String.valueOf(INTERNAL_SERER_ERROR));
            }
        }
    }
}

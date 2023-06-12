package com.mogakko.be_final.domain.mogakkoRoom.service;


import com.mogakko.be_final.domain.members.entity.MemberStatusCode;
import com.mogakko.be_final.domain.members.entity.MemberWeekStatistics;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.repository.MemberWeekStatisticsRepository;
import com.mogakko.be_final.domain.members.repository.MembersRepository;
import com.mogakko.be_final.domain.mogakkoRoom.dto.request.Mogakko12kmRequestDto;
import com.mogakko.be_final.domain.mogakkoRoom.dto.request.MogakkoRoomCreateRequestDto;
import com.mogakko.be_final.domain.mogakkoRoom.dto.request.MogakkoRoomEnterDataRequestDto;
import com.mogakko.be_final.domain.mogakkoRoom.dto.request.MogakkoTimerRequestDto;
import com.mogakko.be_final.domain.mogakkoRoom.dto.response.MogakkoRoomCreateResponseDto;
import com.mogakko.be_final.domain.mogakkoRoom.dto.response.MogakkoRoomReadResponseDto;
import com.mogakko.be_final.domain.mogakkoRoom.dto.response.NeighborhoodResponseDto;
import com.mogakko.be_final.domain.mogakkoRoom.entity.LanguageEnum;
import com.mogakko.be_final.domain.mogakkoRoom.entity.MogakkoRoom;
import com.mogakko.be_final.domain.mogakkoRoom.entity.MogakkoRoomMembers;
import com.mogakko.be_final.domain.mogakkoRoom.repository.MogakkoRoomMembersRepository;
import com.mogakko.be_final.domain.mogakkoRoom.repository.MogakkoRoomRepository;
import com.mogakko.be_final.exception.CustomException;
import com.mogakko.be_final.util.Message;
import com.mogakko.be_final.util.TimeUtil;
import io.openvidu.java.client.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mogakko.be_final.exception.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MogakkoService {

    private final MogakkoRoomRepository mogakkoRoomRepository;
    private final MogakkoRoomMembersRepository mogakkoRoomMembersRepository;
    private final PasswordEncoder passwordEncoder;
    private final MembersRepository membersRepository;
    private final MemberWeekStatisticsRepository memberWeekStatisticsRepository;
    @Value("${OPENVIDU_URL}")
    private String OPENVIDU_URL;

    @Value("${OPENVIDU_SECRET}")
    private String OPENVIDU_SECRET;

    private OpenVidu openvidu;

    @PostConstruct
    public void init() {
        this.openvidu = new OpenVidu(OPENVIDU_URL, OPENVIDU_SECRET);
    }

    // 모각코 방 생성
    @Transactional
    public ResponseEntity<Message> createMogakko(MogakkoRoomCreateRequestDto mogakkoRoomCreateRequestDto, Members member) throws Exception {

        // Session Id, Token 셋팅
        MogakkoRoomCreateResponseDto newToken = createNewToken(member);

        log.info("member 정보 : " + member.getNickname());

        // 모각코방 빌드
        MogakkoRoom mogakkoRoom = MogakkoRoom.builder()
                .sessionId(newToken.getSessionId())
                .title(mogakkoRoomCreateRequestDto.getTitle())
                .masterMemberId(member.getId())
                .maxMembers(mogakkoRoomCreateRequestDto.getMaxMembers())
                .language(mogakkoRoomCreateRequestDto.getLanguage())
                .isOpened(mogakkoRoomCreateRequestDto.getIsOpened())
                .password(passwordEncoder.encode(mogakkoRoomCreateRequestDto.getPassword()))
                .lon(mogakkoRoomCreateRequestDto.getLon())
                .lat(mogakkoRoomCreateRequestDto.getLat())
                .neighborhood(mogakkoRoomCreateRequestDto.getNeighborhood())
                .cntMembers(0L)
                .build();

        log.info("생성된 모각코 방 : " + mogakkoRoom.getTitle());

        // 빌드된 모각코방 저장
        mogakkoRoomRepository.save(mogakkoRoom);
        return new ResponseEntity<>(new Message("모각코방 생성 성공", mogakkoRoom), HttpStatus.OK);
    }

    // 모각코 방 입장
    @Transactional
    public ResponseEntity<Message> enterMogakko(String sessionId, MogakkoRoomEnterDataRequestDto requestDto, Members member) throws OpenViduJavaClientException, OpenViduHttpException {
        // 모각코 방이 있는지 체크
        MogakkoRoom mogakkoRoom = mogakkoRoomRepository.findBySessionId(sessionId).orElseThrow(
                () -> new CustomException(MOGAKKO_NOT_FOUND));

        // 이미 입장한 유저일 경우 예외 발생
        Optional<MogakkoRoomMembers> alreadyEnterMogakkoRoomMembers = mogakkoRoomMembersRepository.findByMemberIdAndMogakkoRoomAndIsEntered(member.getId(), mogakkoRoom, true);
        if (alreadyEnterMogakkoRoomMembers.isPresent()) {
            log.error("===== 이미 입장한 유저임");
            throw new CustomException(ALREADY_ENTER_MEMBER);
        }

        // 방 최대 인원 초과 시 예외발생
        Long chatRoomMaxMembers = mogakkoRoom.getMaxMembers();
        synchronized (mogakkoRoom) {
            mogakkoRoom.updateCntMembers(mogakkoRoom.getCntMembers() + 1);
            if (mogakkoRoom.getCntMembers() > chatRoomMaxMembers) {
                throw new CustomException(MOGAKKO_IS_FULL);
            }
        }

        // 비공개 방일 경우 비밀번호 체크
        if (!mogakkoRoom.isOpened() && !mogakkoRoom.getMasterMemberId().equals(member.getId())) {
            if (requestDto == null) {
                throw new CustomException(PLZ_INPUT_PASSWORD);
            }
            String password = requestDto.getPassword();
            if (password == null || password.equals("")) {
                throw new CustomException(PLZ_INPUT_PASSWORD);
            }
            if (!passwordEncoder.matches(password, mogakkoRoom.getPassword())) {
                throw new CustomException(INVALID_PASSWORD);
            }
        }

        // 해당 방에서 나간 후, 다시 '재접속' 하는 유저
        Optional<MogakkoRoomMembers> reEnterChatRoomMembers = mogakkoRoomMembersRepository.findByMogakkoRoomAndMemberId(mogakkoRoom, member.getId());

        // 방 입장 토큰 생성
        String enterRoomToken = enterRoomCreateSession(member, mogakkoRoom.getSessionId());
        log.info("===== 생성된 토큰 확인 : {}", enterRoomToken);

        MogakkoRoomMembers mogakkoRoomMembers;
        // 재입장 유저의 경우
        if (reEnterChatRoomMembers.isPresent()) {
            log.info("===== 재입장 유저 : {}", member.getNickname());
            mogakkoRoomMembers = reEnterChatRoomMembers.get();
            mogakkoRoomMembers.reEnterRoomMembers(enterRoomToken);
            mogakkoRoom.setDeleted(false);
        } else {
            // 처음 입장하는 유저
            mogakkoRoomMembers = MogakkoRoomMembers.builder()
                    .mogakkoRoom(mogakkoRoom)
                    .memberId(member.getId())
                    .enterRoomToken(enterRoomToken)
                    .isEntered(true)
                    .build();
            log.info("===== 처음 입장 유저 : {}", member.getNickname());
            // 현재 방에 접속한 유저 저장
            mogakkoRoomMembersRepository.save(mogakkoRoomMembers);
        }
        // 모각코 방 정보 저장
        mogakkoRoomRepository.save(mogakkoRoom);
        String token = mogakkoRoomMembers.getEnterRoomToken();
        log.info("===== {} 님 입장 완료", member.getNickname());
        return new ResponseEntity<>(new Message("모각코방 입장 성공", token), HttpStatus.OK);
    }


    // 모각코 방 퇴장
    @Transactional
    public ResponseEntity<Message> outMogakko(String sessionId, Members members) {

        // 모각코 방 존재 확인
        MogakkoRoom mogakkoRoom = mogakkoRoomRepository.findBySessionId(sessionId).orElseThrow(
                () -> new CustomException(MOGAKKO_NOT_FOUND)
        );

        // 방에 멤버가 존재하는지 확인
        MogakkoRoomMembers mogakkoRoomMembers = mogakkoRoomMembersRepository.findByMemberIdAndMogakkoRoomAndIsEntered(members.getId(), mogakkoRoom, true).orElseThrow(
                () -> new CustomException(NOT_MOGAKKO_MEMBER)
        );

        // 유저가 이미 방에서 나감
        if (!mogakkoRoomMembers.isEntered()) {
            throw new CustomException(ALREADY_OUT_MEMBER);
        }

        // 모각코 유저 퇴장 처리
        mogakkoRoomMembers.deleteRoomMembers();

        // 모각코 유저 수 확인
        // 모각코 유저가 0명이라면 방 논리삭제
        synchronized (mogakkoRoom) {
            // 방 인원 카운트 - 1
            mogakkoRoom.updateCntMembers(mogakkoRoom.getCntMembers() - 1);
            mogakkoRoomMembers.isEntered();
            if (mogakkoRoom.getCntMembers() <= 0) {
                // 모각코 삭제처리
                mogakkoRoom.deleteRoom();
                return new ResponseEntity<>(new Message("모각코 퇴장 및 방 삭제 성공", null), HttpStatus.OK);
            }
            // 모각코의 유저 수가 1명 이상있다면 유저 수만 변경
            return new ResponseEntity<>(new Message("모각코 퇴장 성공", null), HttpStatus.OK);
        }
    }

    // 위치 기반 12km 이내 모각코 조회 및 검색
    @Transactional(readOnly = true)
    public ResponseEntity<Message> getAllMogakkosOrSearch(String searchKeyword, String language, Mogakko12kmRequestDto mogakko12KmRequestDto) {
        double lat = mogakko12KmRequestDto.getLat();
        double lon = mogakko12KmRequestDto.getLon();
        List<MogakkoRoom> mogakkoList;
        if (language == null && searchKeyword == null) {
            mogakkoList = mogakkoRoomRepository.findAllByLatAndLon(lat, lon);
        } else if (searchKeyword == null) {
            mogakkoList = mogakkoRoomRepository.findAllByLatAndLonAndLanguage(lat, lon, LanguageEnum.valueOf(language));
        } else if (language == null) {
            mogakkoList = mogakkoRoomRepository.findAllBySearchKeywordAndLatAndLon(searchKeyword, lat, lon);
        } else {
            mogakkoList = mogakkoRoomRepository.findAllBySearchKeywordAndLanguageAndLatAndLon(searchKeyword, LanguageEnum.valueOf(language), lat, lon);
        }
        if (mogakkoList.size() == 0) {
            return new ResponseEntity<>(new Message("근처에 모각코가 없습니다.", null), HttpStatus.OK);
        }
        // 모각코 방 생성으로부터 경과시간 나타내기 위한 코드
        List<MogakkoRoomReadResponseDto> responseDtoList = new ArrayList<>();
        for (MogakkoRoom mr : mogakkoList) {
            long afterSeconds = ChronoUnit.SECONDS.between(mr.getCreatedAt(), LocalDateTime.now());
            String time = TimeUtil.changeSecToTime(afterSeconds);
            MogakkoRoomReadResponseDto responseDto = new MogakkoRoomReadResponseDto(mr, time);
            responseDtoList.add(responseDto);
        }
        return new ResponseEntity<>(new Message("조회 완료", responseDtoList), HttpStatus.OK);
    }

    // 인기 지역 모각코 조회
    @Transactional(readOnly = true)
    public ResponseEntity<Message> topMogakko() {
        List<NeighborhoodResponseDto> mogakkoRoomList = mogakkoRoomRepository.findTop4NeighborhoodsOrderByCountDesc();
        if (mogakkoRoomList.size() >= 4) {
            mogakkoRoomList = mogakkoRoomList.subList(0, 4);
        }
        return new ResponseEntity<>(new Message("인기 지역 모각코 조회 성공", mogakkoRoomList), HttpStatus.OK);
    }

    // 타이머
    @Transactional
    public ResponseEntity<Message> mogakkoTimer(MogakkoTimerRequestDto mogakkoTimerRequestDto, Members member) {
        Time mogakkoTimer;
        if (Long.parseLong(mogakkoTimerRequestDto.getMogakkoTimer().substring(0, 2)) > 23)
            mogakkoTimer = new Time(20, 0, 0);
        else mogakkoTimer = Time.valueOf(mogakkoTimerRequestDto.getMogakkoTimer());
        long timeToSec = mogakkoTimer.toLocalTime().toSecondOfDay();

        int dayOfWeek = LocalDateTime.now().getDayOfWeek().getValue();

        MemberWeekStatistics memberWeekStatistic = memberWeekStatisticsRepository.findById(member.getEmail()).orElseThrow(
                () -> new CustomException(USER_NOT_FOUND)
        );

        switch (dayOfWeek) {
            case 1 -> memberWeekStatistic.addMon(timeToSec);
            case 2 -> memberWeekStatistic.addTue(timeToSec);
            case 3 -> memberWeekStatistic.addWed(timeToSec);
            case 4 -> memberWeekStatistic.addThu(timeToSec);
            case 5 -> memberWeekStatistic.addFri(timeToSec);
            case 6 -> memberWeekStatistic.addSat(timeToSec);
            case 7 -> memberWeekStatistic.addSun(timeToSec);
        }

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

    /**
     * Method
     */

    // 채팅방 생성 시 세션 발급
    public MogakkoRoomCreateResponseDto createNewToken(Members member) throws OpenViduJavaClientException, OpenViduHttpException {

        // 사용자 연결 시 닉네임 전달
        String serverData = member.getNickname();

        // serverData을 사용하여 connectionProperties 객체를 빌드
        ConnectionProperties connectionProperties = new ConnectionProperties.Builder()
                .type(ConnectionType.WEBRTC)
                .data(serverData)
                .build();

        // 새로운 OpenVidu 세션(채팅방) 생성
        Session session = openvidu.createSession();

        return MogakkoRoomCreateResponseDto.builder()
                .sessionId(session.getSessionId()) //리턴해주는 해당 세션아이디로 다른 유저 채팅방 입장시 요청해주시면 됩니다.
                .build();

    }

    // 모각코 입장 시 토큰 발급
    private String enterRoomCreateSession(Members members, String sessionId) throws OpenViduJavaClientException, OpenViduHttpException {

        // 입장하는 유저의 닉네임을 server data에 저장
        String serverData = members.getNickname();

        // serverData을 사용하여 connectionProperties 객체 빌드
        ConnectionProperties connectionProperties = new ConnectionProperties.Builder()
                .type(ConnectionType.WEBRTC)
                .data(serverData)
                .build();

        openvidu.fetch();

        // Openvidu Server에 활성화되어 있는 세션(채팅방) 목록을 가지고 온다.
        List<Session> activeSessionList = openvidu.getActiveSessions();

        // 세션 리스트에서 요청자가 입력한 세션 ID가 일치하는 세션을 찾아서 새로운 토큰을 생성
        // 토큰이 없다면, Openvidu Server에 해당 방이 존재하지 않으므로 예외처리
        Session session = activeSessionList.stream()
                .filter(s -> s.getSessionId().equals(sessionId))
                .findFirst()
                .orElseThrow(() -> new CustomException(MOGAKKO_NOT_FOUND));


        // 해당 채팅방에 프로퍼티스를 설정하면서 커넥션을 만들고, 방에 접속할 수 있는 토큰을 발급한다
        return session.createConnection(connectionProperties).getToken();
    }
}
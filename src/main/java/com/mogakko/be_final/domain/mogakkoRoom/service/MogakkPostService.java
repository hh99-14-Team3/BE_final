package com.mogakko.be_final.domain.mogakkoRoom.service;


import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.mogakkoRoom.dto.request.Mogakko12kmRequestDto;
import com.mogakko.be_final.domain.mogakkoRoom.dto.request.MogakkoRoomCreateRequestDto;
import com.mogakko.be_final.domain.mogakkoRoom.dto.request.MogakkoRoomEnterDataRequestDto;
import com.mogakko.be_final.domain.mogakkoRoom.dto.response.MogakkoRoomCreateResponseDto;
import com.mogakko.be_final.domain.mogakkoRoom.dto.response.MogakkoRoomReadResponseDto;
import com.mogakko.be_final.domain.mogakkoRoom.entity.LanguageEnum;
import com.mogakko.be_final.domain.mogakkoRoom.entity.MogakkoRoom;
import com.mogakko.be_final.domain.mogakkoRoom.entity.MogakkoRoomMembers;
import com.mogakko.be_final.domain.mogakkoRoom.entity.MogakkoRoomMembersLanguageStatistics;
import com.mogakko.be_final.domain.mogakkoRoom.repository.MogakkoRoomMembersLanguageStatisticsRepository;
import com.mogakko.be_final.domain.mogakkoRoom.repository.MogakkoRoomMembersRepository;
import com.mogakko.be_final.domain.mogakkoRoom.repository.MogakkoRoomRepository;
import com.mogakko.be_final.domain.mogakkoRoom.util.MogakkoServiceUtilMethod;
import com.mogakko.be_final.exception.CustomException;
import com.mogakko.be_final.util.BadWordFiltering;
import com.mogakko.be_final.util.Message;
import com.mogakko.be_final.util.TimeUtil;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mogakko.be_final.exception.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MogakkPostService {

    private final PasswordEncoder passwordEncoder;
    private final BadWordFiltering badWordFiltering;
    private final MogakkoRoomRepository mogakkoRoomRepository;
    private final MogakkoRoomMembersRepository mogakkoRoomMembersRepository;
    private final MogakkoRoomMembersLanguageStatisticsRepository mogakkoRoomMembersLanguageStatisticsRepository;
    private final MogakkoServiceUtilMethod mogakkoServiceUtilMethod;

    // 모각코 방 생성
    @Transactional
    public ResponseEntity<Message> createMogakko(MogakkoRoomCreateRequestDto mogakkoRoomCreateRequestDto, Members member) throws Exception {

        // Session Id, Token 셋팅
        MogakkoRoomCreateResponseDto newToken = mogakkoServiceUtilMethod.createNewToken(member);

        log.info("member 정보 : " + member.getNickname());

        // 모각코방 빌드
        MogakkoRoom mogakkoRoom = MogakkoRoom.builder()
                .sessionId(newToken.getSessionId())
                .title(badWordFiltering.checkBadWord(mogakkoRoomCreateRequestDto.getTitle()))
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
        String enterRoomToken = mogakkoServiceUtilMethod.enterRoomCreateSession(member, mogakkoRoom.getSessionId());
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
        // 유저가 선택한 모각코 방 통계 up
        LanguageEnum languageEnum = mogakkoRoom.getLanguage();
        mogakkoRoomMembersLanguageStatisticsRepository.save(MogakkoRoomMembersLanguageStatistics.builder().email(member.getEmail()).language(languageEnum).build());
        log.info("===== {} 님 입장 완료", member.getNickname());
        return new ResponseEntity<>(new Message("모각코방 입장 성공", token), HttpStatus.OK);
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
}
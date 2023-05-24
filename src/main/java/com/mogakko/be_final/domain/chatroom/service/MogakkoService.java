package com.mogakko.be_final.domain.chatroom.service;


import com.mogakko.be_final.domain.chatroom.dto.request.ChatRoomCreateRequestDto;
import com.mogakko.be_final.domain.chatroom.dto.request.ChatRoomEnterDataRequestDto;
import com.mogakko.be_final.domain.chatroom.dto.request.Mogakko5kmRequestDto;
import com.mogakko.be_final.domain.chatroom.dto.response.ChatRoomCreateResponseDto;
import com.mogakko.be_final.domain.chatroom.dto.response.ChatRoomEnterMemberResponseDto;
import com.mogakko.be_final.domain.chatroom.dto.response.ChatRoomEnterMembersResponseDto;
import com.mogakko.be_final.domain.chatroom.entity.ChatRoom;
import com.mogakko.be_final.domain.chatroom.entity.ChatRoomMembers;
import com.mogakko.be_final.domain.chatroom.entity.LanguageEnum;
import com.mogakko.be_final.domain.chatroom.repository.ChatRoomMembersRepository;
import com.mogakko.be_final.domain.chatroom.repository.ChatRoomRepository;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.exception.CustomException;
import com.mogakko.be_final.util.Message;
import io.openvidu.java.client.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mogakko.be_final.exception.ErrorCode.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MogakkoService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMembersRepository chatRoomMembersRepository;

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
    public ResponseEntity<Message> createMogakko(ChatRoomCreateRequestDto chatRoomCreateRequestDto, Members member) throws Exception {

        // Session Id, Token 셋팅
        ChatRoomCreateResponseDto newToken = createNewToken(member);

        log.info("member 정보 : " + member.getNickname());

        // 모각코방 빌드
        ChatRoom chatRoom = ChatRoom.builder()
                .sessionId(newToken.getSessionId())
                .title(chatRoomCreateRequestDto.getTitle())
                .master(member.getNickname())
                .masterMemberId(member.getId())
                .maxMembers(chatRoomCreateRequestDto.getMaxMembers())
                .language(chatRoomCreateRequestDto.getLanguage())
                .isOpened(chatRoomCreateRequestDto.getIsOpened())
                .password(chatRoomCreateRequestDto.getPassword())
                .longitudeX(chatRoomCreateRequestDto.getLongitudeX())
                .latitudeY(chatRoomCreateRequestDto.getLatitudeY())
                .cntMembers(0L)
                .build();

        log.info("생성된 모각코 방 : " + chatRoom.getTitle());

        // 빌드된 모각코방 저장
        chatRoomRepository.save(chatRoom);

        return new ResponseEntity<>(new Message("모각코방 생성 성공", chatRoom), HttpStatus.OK);
    }

    // 모각코 방 입장
    public ResponseEntity<Message> enterMogakko(String SessionId, ChatRoomEnterDataRequestDto requestDto, Members member) throws OpenViduJavaClientException, OpenViduHttpException {
        // 모각코 방이 있는지 체크
        ChatRoom chatRoom = chatRoomRepository.findBySessionId(SessionId).orElseThrow(
                () -> new CustomException(MOGAKKO_NOT_FOUND));

        // 방 최대 인원 초과 시 예외발생
        Long chatRoomMaxMembers = chatRoom.getMaxMembers();
        synchronized (chatRoom) {
            chatRoom.updateCntMembers(chatRoom.getCntMembers() + 1);

            if (chatRoom.getCntMembers() > chatRoomMaxMembers) {
                throw new CustomException(MOGAKKO_IS_FULL);
            }
        }

        // 비공개 방일 경우 비밀번호 체크
        if (!chatRoom.isOpened()) {
            String password = requestDto.getPassword();
            if (password == null) {
                throw new CustomException(PLZ_INPUT_PASSWORD);
            }
            if (!chatRoom.getPassword().equals(password)) {
                throw new CustomException(INVALID_PASSWORD);
            }
        }

        // 이미 입장한 유저일 경우 예외 발생
        Optional<ChatRoomMembers> alreadyEnterChatRoomMembers = chatRoomMembersRepository.findByMemberIdAndSessionIdAndIsEntered(member.getId(), SessionId, false);
        if (alreadyEnterChatRoomMembers.isPresent()) throw new CustomException(ALREADY_ENTER_MEMBER);

        // 해당 방에서 나간 후, 다시 '재접속' 하는 유저
        Optional<ChatRoomMembers> reEnterChatRoomMembers = chatRoomMembersRepository.findBySessionIdAndMemberId(SessionId, member.getId());

        // 방 입장 토큰 생성
        String enterRoomToken = enterRoomCreateSession(member, chatRoom.getSessionId());

        // 재입장 유저의 경우
        if (reEnterChatRoomMembers.isPresent()) {
            ChatRoomMembers chatRoomMembers = reEnterChatRoomMembers.get();
            chatRoomMembers.reEnterRoomMembers(enterRoomToken, member.getNickname());
            chatRoom.setDeleted(false);
            log.info("재입장 유저 stayTime : {}", chatRoomMembers.getRoomStayTime());
        } else {
            // 처음 입장하는 유저
            ChatRoomMembers chatRoomMembers = ChatRoomMembers.builder()
                    .sessionId(chatRoom.getSessionId())
                    .memberId(member.getId())
                    .email(member.getEmail())
                    .profileImage(member.getProfileImage())
                    .enterRoomToken(enterRoomToken)
                    .roomEnterTime(Timestamp.valueOf(LocalDateTime.now()).toLocalDateTime())
                    .roomStayDay(0L)
                    .roomStayTime(Time.valueOf("00:00:00"))
                    .build();

            // 현재 방에 접속한 유저 저장
            chatRoomMembersRepository.save(chatRoomMembers);
        }
        // 모각코 방 정보 저장
        chatRoomRepository.save(chatRoom);
        return new ResponseEntity<>(new Message("모각코방 입장 성공", null), HttpStatus.OK);
    }


    // 모각코 방 퇴장
    public ResponseEntity<Message> outMogakko(String sessionId, Members members, boolean prev) {

        // 모각코 방 존재 확인
        ChatRoom chatRoom = chatRoomRepository.findBySessionIdAndIsDeleted(sessionId, false).orElseThrow(
                () -> new CustomException(MOGAKKO_NOT_FOUND)
        );

        // 방장이 방을 만들고 입장 안함 (뒤로가기) -> 모각코 삭제
        if (prev) {
            synchronized (chatRoom) {
                LocalDateTime roomDeleteTime = LocalDateTime.now();
                chatRoom.deleteRoom(roomDeleteTime);
                return new ResponseEntity<>(new Message("모각코 삭제 성공", null), HttpStatus.OK);
            }
        }

        // 방에 멤버가 존재하는지 확인
        ChatRoomMembers chatRoomMembers = chatRoomMembersRepository.findByMemberIdAndSessionIdAndIsEntered(members.getId(), sessionId, false).orElseThrow(
                () -> new CustomException(NOT_MOGAKKO_MEMBER)
        );

        // 유저가 이미 방에서 나감
        if (chatRoomMembers.isEntered()) {
            throw new CustomException(ALREADY_OUT_MEMBER);
        }

        // 모각코 방에서 얼마나 있었는지 시간 표시
        LocalDateTime chatRoomExitTime = Timestamp.valueOf(LocalDateTime.now()).toLocalDateTime();

        LocalTime start = chatRoomMembers.getRoomEnterTime().toLocalTime();
        LocalTime end = chatRoomExitTime.toLocalTime();

        // 1.기존에 현재방에서 있었던 시간을 가지고 온다, 처음 입장한 유저 = 00:00:00
        LocalTime beforeChatRoomStayTime = chatRoomMembers.getRoomStayTime().toLocalTime();

        // 2.현재방에 들어왔던 시간 - 나가기 버튼 누른 시간 = 머문 시간
        long afterSeconds = ChronoUnit.SECONDS.between(start, end);

        // 3. 1번의 기존 머문 시간에 + 다시 들어왔을때의 머문시간을 더한다.
        // 처음 들어온 유저의 경우 ex) 00:00:00 + 00:05:20
        LocalTime chatRoomStayTime = beforeChatRoomStayTime.plusSeconds(afterSeconds);

        // 일자 계산
        int seconds = beforeChatRoomStayTime.toSecondOfDay();

        Long roomStayDay = chatRoomMembers.getRoomStayDay();
        // 24시간을 넘기면 1일 추가
        if ((seconds + afterSeconds) >= 86400) {
            roomStayDay += 1;
        }

        // 4. 채팅방 유저 논리 삭제, 방에서 나간 시간 저장, 방에 머문 시간 교체
        chatRoomMembers.deleteRoomMembers(chatRoomExitTime, chatRoomStayTime, roomStayDay);

        // 채팅방 유저 수 확인
        // 채팅방 유저가 0명이라면 방 논리삭제
        synchronized (chatRoom) {
            // 방 인원 카운트 - 1
            chatRoom.updateCntMembers(chatRoom.getCntMembers() - 1);

            if (chatRoom.getCntMembers() <= 0) {
                // 방 논리 삭제 + 방 삭제된 시간 기록
                LocalDateTime roomDeleteTime = Timestamp.valueOf(LocalDateTime.now()).toLocalDateTime();
                chatRoom.deleteRoom(roomDeleteTime);
                return new ResponseEntity<>(new Message("모각코 삭제 성공", null), HttpStatus.OK);
            }

            // 모각코의 유저 수가 1명 이상있다면 유저 수만 변경
            return new ResponseEntity<>(new Message("모각코 퇴장 성공", null), HttpStatus.OK);
        }
    }

    // 위치 기반 5km 이내 모각코 조회
//    public ResponseEntity<Message> getAllMogakkos(Mogakko5kmRequestDto requestDto) {
//        double longitudeX = requestDto.getLongitudeX();
//        double latitudeY = requestDto.getLatitudeY();
//        LanguageEnum language = LanguageEnum.valueOf(requestDto.getLanguage());
//        List<ChatRoom> mogakkoList;
//        if (requestDto.getLanguage() == null) {
//            mogakkoList = chatRoomRepository.findAllByLongitudeXAndLatitudeY(latitudeY, longitudeX);
//        } else {
//            mogakkoList = chatRoomRepository.findAllByLongitudeXAndLatitudeYAndLanguage(latitudeY, longitudeX, language.toString());
//        }
//
//        if (mogakkoList.size() == 0) {
//            return new ResponseEntity<>(new Message("근처에 모각코가 없습니다.", null), HttpStatus.OK);
//        }
//        return new ResponseEntity<>(new Message("조회 완료", mogakkoList), HttpStatus.OK);
//    }


    // 모각코 검색
    public ResponseEntity<Message> searchMogakko(String searchKeyword, String language) {
        LanguageEnum languageEnum = LanguageEnum.valueOf(language);
        if (searchKeyword.length() < 1 || searchKeyword.length() > 20) {
            throw new CustomException(INVALID_SEARCH);
        }

        List<ChatRoom> searchedMogakko = chatRoomRepository.findAllBySearchKeyword(searchKeyword, languageEnum.toString());

        if (searchedMogakko.isEmpty()) {
            return new ResponseEntity<>(new Message("검색 결과 없음", null), HttpStatus.OK);
        }
        return new ResponseEntity<>(new Message("모각코 검색 성공", searchedMogakko), HttpStatus.OK);
    }

    // 모각코에 있는 유저 정보 조회
    public ResponseEntity<Message> getMogakkoMembersData(String SessionId, Members members) {
        // 모각코 유무 확인
        ChatRoom chatRoom = chatRoomRepository.findBySessionIdAndIsDeleted(SessionId, false).orElseThrow(
                () -> new CustomException(MOGAKKO_NOT_FOUND));

        // 유저가 모각코에 있는지 확인
        chatRoomMembersRepository.findByMemberIdAndSessionIdAndIsEntered(members.getId(), SessionId, false).orElseThrow(
                () -> new CustomException(NOT_MOGAKKO_MEMBER)
        );

        boolean chatRoomMaster;
        boolean chatRoomNowMembers;

        // 모각코 유저들 Entity
        List<ChatRoomMembers> chatRoomMembersList = chatRoomMembersRepository.findAllBySessionIdAndIsEntered(chatRoom.getSessionId(), false);

        // 모각코 유저들 Dto
        List<ChatRoomEnterMemberResponseDto> chatRoomMemberListResponseDto = new ArrayList<>();

        // 모각코 유저들 Entity -> DTO
        // 방장 정보 및 현재 접속한 유저 설정
        for (ChatRoomMembers chatRoomMembers : chatRoomMembersList) {
            // 모각코 방장 체크
            if (members != null && chatRoom.getMasterMemberId().equals(chatRoomMembers.getMemberId())) {
                chatRoomMaster = true;
            } else {
                chatRoomMaster = false;
            }
            // 모각코에 있는 멤버 체크
            if (members != null && chatRoomMembers.getMemberId().equals((members.getId()))) {
                chatRoomNowMembers = true;
            } else {
                chatRoomNowMembers = false;
            }
            chatRoomMemberListResponseDto.add(new ChatRoomEnterMemberResponseDto(chatRoomMembers, chatRoomMaster, chatRoomNowMembers));
        }
        ChatRoomEnterMembersResponseDto chatRoomResponseDto = new ChatRoomEnterMembersResponseDto(chatRoom, chatRoomMemberListResponseDto);

        return new ResponseEntity<>(new Message("유저 정보 조회 성공", chatRoomResponseDto), HttpStatus.OK);
    }

    // 채팅방 생성 시 세션 발급
    private ChatRoomCreateResponseDto createNewToken(Members member) throws OpenViduJavaClientException, OpenViduHttpException {

        // 사용자 연결 시 닉네임 전달
        String serverData = member.getNickname();


        // serverData을 사용하여 connectionProperties 객체를 빌드
        ConnectionProperties connectionProperties = new ConnectionProperties.Builder()
                .type(ConnectionType.WEBRTC)
                .data(serverData)
                .build();

        // 새로운 OpenVidu 세션(채팅방) 생성
        Session session = openvidu.createSession();


        return ChatRoomCreateResponseDto.builder()
                .sessionId(session.getSessionId()) //리턴해주는 해당 세션아이디로 다른 유저 채팅방 입장시 요청해주시면 됩니다.
                .build();

    }


    /**
     * Method
     */

    // 모각코 입장 시 토큰 발급
    private String enterRoomCreateSession(Members members, String sessionId) throws OpenViduJavaClientException, OpenViduHttpException {

        // 입장하는 유저의 닉네임을 server data에 저장
        String serverData = members.getNickname();

        // serverData을 사용하여 connectionProperties 객체 빌드
        ConnectionProperties connectionProperties
                = new ConnectionProperties.Builder().type(ConnectionType.WEBRTC).data(serverData).build();

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
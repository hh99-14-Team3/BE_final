package com.mogakko.be_final.domain.chatroom.service;

import com.mogakko.be_final.domain.chatroom.entity.ChatRoom;
import com.mogakko.be_final.domain.chatroom.entity.ChatRoomMembers;
import com.mogakko.be_final.domain.chatroom.repository.ChatRoomRepository;
import com.mogakko.be_final.domain.chatroom.repository.ChatRoomMembersRepository;
import io.openvidu.java.client.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatRoomScheduler {


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


    @Async
    @Scheduled(fixedRate = 300000)/*5분에 1번*/
    @Transactional
    public void chatRoomZeroUserDeleteScheduler() throws OpenViduJavaClientException, OpenViduHttpException {
        /*스케줄러를 사용하여, 무언가 버그로 인하여 삭제되지 않은 채팅방과, 그방에 속한 유저를 정리해주는 코드 입니다.*/

        /*DB에 저장된 삭제되지 않은 모든 채팅방을 불러온다.*/
        List<ChatRoom> all = chatRoomRepository.findAllByIsDeleted(false);

        /*오픈비듀 서버에서 세션(채팅방)정보를 불러온다.*/
        openvidu.fetch();

        /*DB랑 오픈비듀랑 비교한다.*/
        for (ChatRoom chatRoom : all) {
            Session activeSession = openvidu.getActiveSession(chatRoom.getSessionId());

            if (null == activeSession && !chatRoom.getMaster().equals("두런두런")) {/*더미 방은 제외하고 삭제!*/
//                log.info("❌❌ " + chatRoom.getSessionId() + "는 Openvidu에 존재하지않습니다..");

                List<ChatRoomMembers> chatRoomMembersList
                        = chatRoomMembersRepository.findAllBySessionIdAndIsEntered(chatRoom.getSessionId(), false);


                /*해당 채팅방에서 얼마나 있었는지 시간 표시 기능 구현*/
                /*방에서 나간 시간 저장.*/
                LocalDateTime chatRoomExitTime = Timestamp.valueOf(LocalDateTime.now()).toLocalDateTime();
                LocalTime end = chatRoomExitTime.toLocalTime();

                /*방에서 나갔지만 버그로 인하여 처리가 안되어 안나간것으로 처리되어 있는 유저들*/
                for (ChatRoomMembers chatRoomMembers : chatRoomMembersList) {
//                    log.info("❌❌❌ " + chatRoomUser.getNickname() + "는 이미 방에서 나간 유저 입니다. -> 방 나가기 로직을 진행 합니다.");

                    LocalTime start = chatRoomMembers.getRoomEnterTime().toLocalTime();

                    /*1.기존에 현재방에서 있었던 시간을 가지고 온다, 처음 입장한 유저 = 00:00:00 */
                    LocalTime beforeChatRoomStayTime = chatRoomMembers.getRoomStayTime().toLocalTime();

                    /*2.현재방에 들어왔던 시간 - 나가기 버튼 누른 시간 = 머문 시간*/
                    long afterSeconds = ChronoUnit.SECONDS.between(start, end);

                    /*3. 1번의 기존 머문 시간에 + 다시 들어왔을때의 머문시간을 더한다.
                     * 처음 들어온 유저의 경우 ex) 00:00:00 + 00:05:20  */
                    LocalTime chatRoomStayTime = beforeChatRoomStayTime.plusSeconds(afterSeconds);/*시간 계산*/

                    /*일자 계산*/
                    int seconds = beforeChatRoomStayTime.toSecondOfDay();

                    Long roomStayDay = chatRoomMembers.getRoomStayDay();
                    if ((seconds + afterSeconds) >= 86400) {/*24시간을 넘기면 1일 추가*/
                        roomStayDay += 1;
                    }
                    /*4. 채팅방 유저 논리 삭제, 방에서 나간 시간 저장, 방에 머문 시간 교체*/
                    chatRoomMembers.deleteRoomMembers(chatRoomExitTime, chatRoomStayTime, roomStayDay);
                }


                /*허구의 방이므로 해당 방 데이터들 삭제!*/
                /*방 인원 카운트 - 1*/
                chatRoom.updateCntMembers(0L);

                /*방 논리 삭제 + 방 삭제된 시간 기록*/
                LocalDateTime roomDeleteTime = Timestamp.valueOf(LocalDateTime.now()).toLocalDateTime();
                chatRoom.deleteRoom(roomDeleteTime);
                log.info("❌❌❌❌ " + chatRoom.getSessionId() + "는 Openvidu에 존재하지 않는 허구의 방이므로 삭제가 완료 되었습니다.");


            } else {
//                log.info("⭕⭕ " + activeSession.getSessionId() + "는 Openvidu에 존재합니다.");
            }

        }


    }

}

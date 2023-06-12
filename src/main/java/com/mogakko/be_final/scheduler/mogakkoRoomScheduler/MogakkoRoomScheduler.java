package com.mogakko.be_final.scheduler.mogakkoRoomScheduler;

import com.mogakko.be_final.domain.mogakkoRoom.entity.MogakkoRoom;
import com.mogakko.be_final.domain.mogakkoRoom.entity.MogakkoRoomMembers;
import com.mogakko.be_final.domain.mogakkoRoom.repository.MogakkoRoomMembersRepository;
import com.mogakko.be_final.domain.mogakkoRoom.repository.MogakkoRoomRepository;
import io.openvidu.java.client.OpenVidu;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import io.openvidu.java.client.Session;
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

/**
 * 스케줄러를 사용하여, 삭제되지 않은 모각코 방과 그 모각코에 속한 유저를 정리
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class MogakkoRoomScheduler {


    private final MogakkoRoomRepository mogakkoRoomRepository;
    private final MogakkoRoomMembersRepository mogakkoRoomMembersRepository;

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
    @Scheduled(fixedRate = 300000) // 1회/5분
    @Transactional
    public void chatRoomZeroUserDeleteScheduler() throws OpenViduJavaClientException, OpenViduHttpException {

        // DB에 저장된 삭제되지 않은 모든 채팅방 조회
        List<MogakkoRoom> allMogakkoRooms = mogakkoRoomRepository.findAllByIsDeleted(false);

        // Openvidu 서버에서 세션(모각코)정보 조회
        openvidu.fetch();

        // DB - Openvidu 비교
        for (MogakkoRoom mogakkoRoom : allMogakkoRooms) {
            Session activeSession = openvidu.getActiveSession(mogakkoRoom.getSessionId());
            // 더미 방은 제외하고 삭제
            if (activeSession == null && !mogakkoRoom.getMasterMemberId().equals(0)) {
                log.info("===== { " + mogakkoRoom.getSessionId() + " } 는 Openvidu에 존재하지않습니다.");

                List<MogakkoRoomMembers> mogakkoRoomMembersList = mogakkoRoomMembersRepository.findAllByMogakkoRoomAndIsEntered(mogakkoRoom, false);

                // 방에서 나갔지만 기타 버그로 인해 입장중으로 인식되는 유저 정리
                for (MogakkoRoomMembers mogakkoRoomMember : mogakkoRoomMembersList) {
                    log.info("===== { " + mogakkoRoomMember.getMemberId() + " } 는 이미 모각코에서 나간 유저 입니다. -> 모각코 퇴장 처리");

                    // 모각코 유저 삭제처리
                    mogakkoRoomMember.deleteRoomMembers();
                }

                // 허구의 방이므로 해당 모각코 데이터 삭제
                // 참여 인원 카운트
                mogakkoRoom.updateCntMembers(0L);

                // 모각코 논리 삭제 (방 삭제된 시간 기록)
                mogakkoRoom.deleteRoom();
                log.info("===== { " + mogakkoRoom.getSessionId() + " } 는 Openvidu에 없는 모각코입니다. -> 모각코 삭제 처리");
            } else {
                log.info("==== { " + activeSession.getSessionId() + " } 는 Openvidu에 존재합니다.");
            }
        }
    }
}

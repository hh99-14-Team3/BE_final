package com.mogakko.be_final.domain.mogakkoRoom.util;

import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.mogakkoRoom.dto.response.MogakkoRoomCreateResponseDto;
import com.mogakko.be_final.exception.CustomException;
import io.openvidu.java.client.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

import static com.mogakko.be_final.exception.ErrorCode.MOGAKKO_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class MogakkoServiceUtilMethod {


    @Value("${OPENVIDU_URL}")
    private String OPENVIDU_URL;

    @Value("${OPENVIDU_SECRET}")
    private String OPENVIDU_SECRET;

    private OpenVidu openvidu;

    @PostConstruct
    public void init() {
        this.openvidu = new OpenVidu(OPENVIDU_URL, OPENVIDU_SECRET);
    }

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
    public String enterRoomCreateSession(Members members, String sessionId) throws OpenViduJavaClientException, OpenViduHttpException {

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

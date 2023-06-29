package com.mogakko.be_final.domain.mogakkoRoom.util;

import com.mogakko.be_final.domain.members.entity.MemberStatusCode;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.entity.Role;
import com.mogakko.be_final.domain.mogakkoRoom.dto.response.MogakkoRoomCreateResponseDto;
import com.mogakko.be_final.exception.CustomException;
import io.openvidu.java.client.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.mogakko.be_final.exception.ErrorCode.MOGAKKO_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@DisplayName("MogakkoServiceUtilMethod 테스트")
@ExtendWith({MockitoExtension.class})
class MogakkoServiceUtilMethodTest {
    @Mock
    OpenVidu openvidu;
    @InjectMocks
    MogakkoServiceUtilMethod mogakkoServiceUtilMethod;

    Members member = Members.builder()
            .email("test@example.com")
            .nickname("nickname")
            .password("password1!")
            .role(Role.USER)
            .codingTem(36.5)
            .mogakkoTotalTime(0L)
            .memberStatusCode(MemberStatusCode.BASIC)
            .profileImage("https://source.boringavatars.com/beam/120/$" + "nickname" + "?colors=00F0FF,172435,394254,EAEBED,F9F9FA")
            .friendCode(123456)
            .isTutorialCheck(false)
            .build();

    @DisplayName("모각코 방 생성 시 세션 발급 테스트")
    @Test
    void createNewToken() throws OpenViduJavaClientException, OpenViduHttpException {
        // Given
        Session session = mock(Session.class);
        when(openvidu.createSession()).thenReturn(session);
        String sessionId = "sessionId";
        when(session.getSessionId()).thenReturn(sessionId);

        // When
        MogakkoRoomCreateResponseDto responseDto = mogakkoServiceUtilMethod.createNewToken(member);

        // Then
        assertEquals(sessionId, responseDto.getSessionId());
        verify(openvidu).createSession();
        verify(session).getSessionId();
    }

    @DisplayName("모각코 입장 시 토큰 발급 성공 테스트")
    @Test
    void enterRoomCreateSession() throws OpenViduJavaClientException, OpenViduHttpException {
        // Given
        Session session = mock(Session.class);
        Connection connection = mock(Connection.class);
        String sessionId = "sessionId";
        String token = "token";

        when(session.getSessionId()).thenReturn(sessionId);
        when(openvidu.getActiveSessions()).thenReturn(Collections.singletonList(session));
        when(session.createConnection(any(ConnectionProperties.class))).thenReturn(connection);
        when(connection.getToken()).thenReturn(token);

        // When
        String response = mogakkoServiceUtilMethod.enterRoomCreateSession(member, sessionId);

        // Then
        assertEquals(token, response);
        verify(session).getSessionId();
        verify(openvidu).getActiveSessions();
        verify(session).createConnection(any(ConnectionProperties.class));
        verify(connection).getToken();
    }

    @DisplayName("모각코 입장 시 토큰 발급 예외 테스트")
    @Test
    void enterRoomCreateSession_failWithNotFound() throws OpenViduJavaClientException, OpenViduHttpException {
        // Given
        Session session = mock(Session.class);
        Connection connection = mock(Connection.class);
        String sessionId = "sessionId";
        String token = "token";

        List<Session> sessionList = new ArrayList<>();
        sessionList.add(session);

        when(openvidu.getActiveSessions()).thenReturn(sessionList);
        when(session.getSessionId()).thenReturn("noSessionId");

        // When & Then
        CustomException customException = assertThrows(CustomException.class, () -> mogakkoServiceUtilMethod.enterRoomCreateSession(member, sessionId));
        assertEquals(MOGAKKO_NOT_FOUND, customException.getErrorCode());
    }
}
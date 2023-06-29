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

import java.util.Collections;

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

  
}
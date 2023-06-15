package com.mogakko.be_final.domain.mogakkoRoom.service;

import com.mogakko.be_final.domain.members.entity.MemberStatusCode;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.entity.Role;
import com.mogakko.be_final.domain.members.entity.SocialType;
import com.mogakko.be_final.domain.mogakkoRoom.dto.request.MogakkoRoomCreateRequestDto;
import com.mogakko.be_final.domain.mogakkoRoom.entity.LanguageEnum;
import com.mogakko.be_final.domain.mogakkoRoom.repository.MogakkoRoomRepository;
import com.mogakko.be_final.util.Message;
import io.openvidu.java.client.OpenVidu;
import io.openvidu.java.client.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class MogakkoServiceTest {

    @Mock
    private MogakkoRoomRepository mogakkoRoomRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Value("${OPENVIDU_URL}")
    private String OPENVIDU_URL;

    @Value("${OPENVIDU_SECRET}")
    private String OPENVIDU_SECRET;

    @Mock
    private OpenVidu openvidu;

    @InjectMocks
    private MogakkoService mogakkoService;
    private Members member;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        member = Members.builder()
                .id(1L)
                .email("test@example.com")
                .nickname("nickname")
                .memberStatusCode(MemberStatusCode.BASIC)
                .mogakkoTotalTime(0L)
                .githubId("github")
                .profileImage("image")
                .role(Role.USER)
                .socialUid("id")
                .socialType(SocialType.GOOGLE)
                .password("1q2w3e4r")
                .build();
    }

    @DisplayName("모각코 방 생성 성공 TEST")
    @Test
    void createMogakko_Success() throws Exception {
        // given
        MogakkoRoomCreateRequestDto requestDto = new MogakkoRoomCreateRequestDto();
        requestDto.setTitle("코딩킹");
        requestDto.setLanguage(LanguageEnum.JAVA);
        requestDto.setLat(34.33333);
        requestDto.setLon(125.333344);
        requestDto.setMaxMembers(4L);
        requestDto.setNeighborhood("가남읍");
        requestDto.setIsOpened(false);
        requestDto.setPassword("3232");

        Session session = mock(Session.class);

        // when
        when(openvidu.createSession()).thenReturn(session);
        ResponseEntity<Message> response = mogakkoService.createMogakko(requestDto, member);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("모각코방 생성 성공", response.getBody().getMessage());
        verify(openvidu).createSession();
    }


    @Test
    void enterMogakko() {
    }

    @Test
    void outMogakko() {
    }

    @Test
    void getAllMogakkosOrSearch() {
    }

    @Test
    void getMogakkoMembersData() {
    }

    @Test
    void topMogakko() {
    }

    @Test
    void mogakkoTimer() {
    }

    @Test
    void totalTimer() {
    }

    @Test
    void totalTimeTypeLong() {
    }

    @Test
    void changeSecToTime() {
    }

}

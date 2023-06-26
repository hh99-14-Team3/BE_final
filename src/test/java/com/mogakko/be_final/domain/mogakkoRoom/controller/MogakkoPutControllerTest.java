package com.mogakko.be_final.domain.mogakkoRoom.controller;

import com.mogakko.be_final.domain.members.entity.MemberStatusCode;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.entity.Role;
import com.mogakko.be_final.domain.mogakkoRoom.dto.request.MogakkoTimerRequestDto;
import com.mogakko.be_final.domain.mogakkoRoom.service.MogakkoPutService;
import com.mogakko.be_final.userDetails.UserDetailsImpl;
import com.mogakko.be_final.util.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.sql.Time;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Mogakko Controller - [PUT] 테스트")
class MogakkoPutControllerTest {
    @Mock
    private MogakkoPutService mogakkoPutService;
    @Mock
    private MogakkoTimerRequestDto mogakkoTimerRequestDto;
    @InjectMocks
    private MogakkoPutController mogakkoPutController;
    private MockMvc mockMvc;
    private Time mogakkoTimer;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(mogakkoPutController).build();
    }

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

    @DisplayName("[PUT] 모각코 타이머 테스트")
    @Test
    void mogakkoTimer() {
        UserDetailsImpl userDetails = new UserDetailsImpl(member, member.getEmail());
        Message expectedMessage = new Message("저장 성공", mogakkoTimer);

        when(mogakkoPutService.mogakkoTimer(any(MogakkoTimerRequestDto.class), any(Members.class))).thenReturn(ResponseEntity.ok(expectedMessage));

        ResponseEntity<Message> response = mogakkoPutController.mogakkoTimer(mogakkoTimerRequestDto, userDetails);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody());
    }
}
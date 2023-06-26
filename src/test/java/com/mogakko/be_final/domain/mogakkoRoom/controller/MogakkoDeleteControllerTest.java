package com.mogakko.be_final.domain.mogakkoRoom.controller;

import com.mogakko.be_final.domain.members.entity.MemberStatusCode;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.entity.Role;
import com.mogakko.be_final.domain.mogakkoRoom.entity.LanguageEnum;
import com.mogakko.be_final.domain.mogakkoRoom.entity.MogakkoRoom;
import com.mogakko.be_final.domain.mogakkoRoom.service.MogakkoDeleteService;
import com.mogakko.be_final.userDetails.UserDetailsImpl;
import com.mogakko.be_final.util.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("Mogakko Controller - [DELETE] 테스트")
class MogakkoDeleteControllerTest {
    @Mock
    private MogakkoDeleteService mogakkoDeleteService;
    @InjectMocks
    private MogakkoDeleteController mogakkoDeleteController;
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(mogakkoDeleteController).build();
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

    MogakkoRoom mogakkoRoom = MogakkoRoom.builder()
            .sessionId("ses_A1zOZCFwIL")
            .title("코딩킹")
            .language(LanguageEnum.JAVA)
            .lat(11.111111)
            .lon(11.111111)
            .maxMembers(4L)
            .neighborhood("우리동네")
            .isOpened(false)
            .password("1111")
            .cntMembers(1L)
            .masterMemberId(1L)
            .build();


    @DisplayName("[DELETE] 모각코 방 퇴장 테스트")
    @Test
    void outClickMogakko() throws Exception {
        UserDetailsImpl userDetails = new UserDetailsImpl(member, member.getEmail());
        String sessionId = mogakkoRoom.getSessionId();
        Message expectedMessage = new Message("모각코 퇴장 성공", null);

        when(mogakkoDeleteService.outMogakko(anyString(), any(Members.class))).thenReturn(ResponseEntity.ok(expectedMessage));

        mockMvc.perform(MockMvcRequestBuilders.delete("/mogakko/{sessionId}", sessionId)
                        .principal(userDetails))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage.getMessage()));

        verify(mogakkoDeleteService, times(1)).outMogakko(sessionId, userDetails.getMember());
    }
}
package com.mogakko.be_final.domain.mogakkoRoom.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mogakko.be_final.domain.members.controller.MembersPostController;
import com.mogakko.be_final.domain.members.dto.request.SignupRequestDto;
import com.mogakko.be_final.domain.members.entity.MemberStatusCode;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.entity.Role;
import com.mogakko.be_final.domain.mogakkoRoom.dto.request.MogakkoRoomCreateRequestDto;
import com.mogakko.be_final.domain.mogakkoRoom.dto.request.MogakkoRoomEnterDataRequestDto;
import com.mogakko.be_final.domain.mogakkoRoom.entity.LanguageEnum;
import com.mogakko.be_final.domain.mogakkoRoom.entity.MogakkoRoom;
import com.mogakko.be_final.domain.mogakkoRoom.service.MogakkoPostService;
import com.mogakko.be_final.userDetails.UserDetailsImpl;
import com.mogakko.be_final.util.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Mogakko Controller - [POST] 테스트")
class MogakkoPostControllerTest {
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private MogakkoPostService mogakkoPostService;
    @Mock
    private MogakkoRoomCreateRequestDto mogakkoRoomCreateRequestDto;
    @Mock
    private MogakkoRoomEnterDataRequestDto mogakkoRoomEnterDataRequestDto;
    @InjectMocks
    private MogakkoPostController mogakkoPostController;
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mogakkoPostController = new MogakkoPostController(mogakkoPostService);
        mockMvc = MockMvcBuilders.standaloneSetup(mogakkoPostController).build();
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

    @DisplayName("[POST] 모각코 방 생성 테스트")
    @Test
    void createMogakko() throws Exception {
        UserDetailsImpl userDetails = new UserDetailsImpl(member, member.getEmail());

        MogakkoRoomCreateRequestDto requestDto = MogakkoRoomCreateRequestDto.builder()
                .title("코딩킹")
                .language(LanguageEnum.JAVA)
                .lat(11.111111)
                .lon(11.111111)
                .maxMembers(4L)
                .neighborhood("우리동네")
                .isOpened(false)
                .password("1111")
                .build();

        Message message = new Message("모각코방 생성 성공", mogakkoRoom);
        when(mogakkoPostService.createMogakko(any(MogakkoRoomCreateRequestDto.class), any(Members.class))).thenReturn(ResponseEntity.ok(message));

        ResponseEntity<Message> response = mogakkoPostController.createMogakko(requestDto, userDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(message, response.getBody());
    }

}
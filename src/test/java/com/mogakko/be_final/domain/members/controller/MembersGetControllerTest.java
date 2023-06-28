package com.mogakko.be_final.domain.members.controller;

import com.mogakko.be_final.domain.members.dto.response.BestMembersResponseDto;
import com.mogakko.be_final.domain.members.dto.response.MemberPageResponseDto;
import com.mogakko.be_final.domain.members.dto.response.MemberSimpleResponseDto;
import com.mogakko.be_final.domain.members.entity.MemberStatusCode;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.entity.Role;
import com.mogakko.be_final.domain.members.service.MembersGetService;
import com.mogakko.be_final.userDetails.UserDetailsImpl;
import com.mogakko.be_final.util.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Members Controller - [GET] 테스트")
class MembersGetControllerTest {
    @Mock
    private MembersGetService membersGetService;
    @Mock
    private MemberPageResponseDto memberPageResponseDto;
    @Mock
    private MemberSimpleResponseDto memberSimpleResponseDto;
    @InjectMocks
    private MembersGetController membersGetController;

    private MockMvc mockMvc;

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

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(membersGetController).build();
    }

    @DisplayName("[GET] 이메일 중복 체크 테스트")
    @Test
    void checkEmail() throws Exception {
        String email = "test@example.com";
        Message expectedMessage = new Message("중복 확인 성공", null);

        when(membersGetService.checkEmail(Mockito.anyString())).thenReturn(ResponseEntity.ok(expectedMessage));

        mockMvc.perform(MockMvcRequestBuilders.get("/members/signup/checkEmail")
                        .param("email", email))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage.getMessage()));

        verify(membersGetService, times(1)).checkEmail(email);
    }

    @DisplayName("[GET] 닉네임 중복 체크 테스트")
    @Test
    void checkNickname() throws Exception {
        String nickname = "testuser";
        Message expectedMessage = new Message("중복 확인 성공", null);

        when(membersGetService.checkNickname(Mockito.anyString())).thenReturn(ResponseEntity.ok(expectedMessage));

        mockMvc.perform(MockMvcRequestBuilders.get("/members/signup/checkNickname")
                        .param("nickname", nickname))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage.getMessage()));

        verify(membersGetService, times(1)).checkNickname(nickname);
    }

    @DisplayName("[GET] 마이페이지 조회 테스트")
    @Test
    void myPage() throws Exception {
        UserDetailsImpl userDetails = new UserDetailsImpl(member, member.getEmail());

        Message expectedMessage = new Message("마이페이지 조회 성공", memberPageResponseDto);

        when(membersGetService.readMyPage(any(Members.class))).thenReturn(ResponseEntity.ok(expectedMessage));

        mockMvc.perform(MockMvcRequestBuilders.get("/members/mypage")
                        .principal(userDetails))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage.getMessage()));

        verify(membersGetService, times(1)).readMyPage(member);
    }

    @DisplayName("[GET] 다른 유저 프로필 조회 테스트")
    @Test
    void getMemberProfile() throws Exception {
        UserDetailsImpl userDetails = new UserDetailsImpl(member, member.getEmail());
        Long memberId = 1L;
        Message expectedMessage = new Message("프로필 조회 성공", memberPageResponseDto);

        when(membersGetService.getMemberProfile(any(Members.class), eq(memberId))).thenReturn(ResponseEntity.ok(expectedMessage));

        mockMvc.perform(MockMvcRequestBuilders.get("/members/{memberId}", memberId)
                        .principal(userDetails))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage.getMessage()));

        verify(membersGetService, times(1)).getMemberProfile(userDetails.getMember(), memberId);
    }

    @DisplayName("[GET] 다른 유저 닉네임으로 검색 테스트")
    @Test
    void searchMembersByNickname() throws Exception {
        UserDetailsImpl userDetails = new UserDetailsImpl(member, member.getEmail());
        String nickname = "testuser";
        Message expectedMessage = new Message("멤버 검색 성공", member);

        when(membersGetService.searchMembersByNickname(anyString(), any(Members.class))).thenReturn(ResponseEntity.ok(expectedMessage));

        mockMvc.perform(MockMvcRequestBuilders.get("/members/search/nickname")
                        .param("nickname", nickname)
                        .principal(userDetails))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage.getMessage()));

        verify(membersGetService, times(1)).searchMembersByNickname(nickname, userDetails.getMember());
    }

    @DisplayName("[GET] 다른 유저 친구코드로 검색 테스트")
    @Test
    void searchMemberByFriendsCode() throws Exception {
        UserDetailsImpl userDetails = new UserDetailsImpl(member, member.getEmail());
        int friendcode = 123456;
        Message expectedMessage = new Message("멤버 검색 성공", memberSimpleResponseDto);

        when(membersGetService.searchMemberByFriendsCode(anyString(), any(Members.class))).thenReturn(ResponseEntity.ok(expectedMessage));

        mockMvc.perform(MockMvcRequestBuilders.get("/members/search/friend-code")
                        .param("friendCode", String.valueOf(friendcode))
                        .principal(userDetails))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage.getMessage()));

        verify(membersGetService, times(1)).searchMemberByFriendsCode(String.valueOf(friendcode), userDetails.getMember());
    }

    @DisplayName("[GET] 최고의 유저 조회 테스트")
    @Test
    void readBestMembers() throws Exception {
        List<BestMembersResponseDto> bestMembersList = new ArrayList<>();
        Message expectedMessage = new Message("최고의 ON:s 조회 성공", bestMembersList);

        when(membersGetService.readBestMembers()).thenReturn(ResponseEntity.ok(expectedMessage));

        mockMvc.perform(MockMvcRequestBuilders.get("/members/best"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(expectedMessage.getMessage()));

        verify(membersGetService, times(1)).readBestMembers();
    }
}
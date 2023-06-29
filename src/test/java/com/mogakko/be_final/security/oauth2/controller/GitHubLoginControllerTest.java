package com.mogakko.be_final.security.oauth2.controller;

import com.mogakko.be_final.domain.members.entity.MemberStatusCode;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.entity.Role;
import com.mogakko.be_final.security.oauth2.service.GitHubLoginService;
import com.mogakko.be_final.userDetails.UserDetailsImpl;
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
import org.springframework.web.servlet.view.RedirectView;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("GitHub Login Controller - [GET] 테스트")
@ExtendWith(MockitoExtension.class)
class GitHubLoginControllerTest {
    @Mock
    GitHubLoginService githubLoginService;
    @InjectMocks
    GitHubLoginController gitHubLoginController;
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(gitHubLoginController).build();
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

    UserDetailsImpl userDetails = new UserDetailsImpl(member, member.getEmail());

    @DisplayName("[GET] 깃허브 인증 요청 테스트")
    @Test
    void startGithubLogin() throws Exception {
        // Given
        String url = "url";
        Map<String, String> response = new HashMap<>();
        response.put("url", url);
        when(githubLoginService.startGithubLogin(any(Members.class))).thenReturn(ResponseEntity.ok(response));

        // When
        mockMvc.perform(MockMvcRequestBuilders.get("/oauth2/githubLogin")
                        .principal(userDetails))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Then
        verify(githubLoginService, times(1)).startGithubLogin(member);
    }

}
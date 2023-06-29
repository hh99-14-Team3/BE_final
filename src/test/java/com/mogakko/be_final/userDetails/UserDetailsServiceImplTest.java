package com.mogakko.be_final.userDetails;

import com.mogakko.be_final.domain.members.entity.MemberStatusCode;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.entity.Role;
import com.mogakko.be_final.domain.members.repository.MembersRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@DisplayName("User Details Service Impl 테스트")
@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    MembersRepository membersRepository;
    @InjectMocks
    UserDetailsServiceImpl userDetailsServiceimpl;

    Members member = Members.builder()
            .id(1L)
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


    @DisplayName("loadUserByUsername 테스트")
    @Test
    void loadUserByUsername() {
        when(membersRepository.findByEmail(member.getEmail())).thenReturn(Optional.of(member));
        UserDetails response = userDetailsServiceimpl.loadUserByUsername(member.getEmail());
        assertNull(null, response.getUsername());
    }

    @DisplayName("UsernameNotFoundException 예외 테스트")
    @Test
    void loadUserByUsername_fail() {
        when(membersRepository.findByEmail(member.getEmail())).thenReturn(Optional.empty());
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> userDetailsServiceimpl.loadUserByUsername(member.getEmail()));
        assertEquals(exception.getMessage(), "사용자를 찾을 수 없습니다.");
    }
}
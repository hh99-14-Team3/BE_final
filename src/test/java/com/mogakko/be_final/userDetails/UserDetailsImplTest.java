package com.mogakko.be_final.userDetails;

import com.mogakko.be_final.domain.members.entity.MemberStatusCode;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.entity.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.security.auth.Subject;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User Details Impl 테스트")
@ExtendWith(MockitoExtension.class)
class UserDetailsImplTest {

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

    UserDetailsImpl userDetails = new UserDetailsImpl(member, member.getEmail());

    @Nested
    @DisplayName("UserDetails Getter 테스트")
    class userDetailsGetterTest {
        @DisplayName("getMember 테스트")
        @Test
        void getMember() {
            Members member1 = userDetails.getMember();
            assertEquals(userDetails.getMember(), member1);
        }

        @DisplayName("getEmail 테스트")
        @Test
        void getEmail() {
            String email = member.getEmail();
            assertEquals(userDetails.getEmail(), email);
        }

        @DisplayName("getAuthorities 테스트")
        @Test
        void getAuthorities() {
            assertNull(userDetails.getAuthorities());
        }

        @DisplayName("getPassword 테스트")
        @Test
        void getPassword() {
            assertNull(userDetails.getPassword());
        }

        @DisplayName("getUsername 테스트")
        @Test
        void getUsername() {
            assertNull(userDetails.getUsername());
        }

        @DisplayName("isAccountNonExpired 테스트")
        @Test
        void isAccountNonExpired() {
            assertFalse(userDetails.isAccountNonExpired());
        }

        @DisplayName("isAccountNonLocked 테스트")
        @Test
        void isAccountNonLocked() {
            assertFalse(userDetails.isAccountNonLocked());
        }

        @DisplayName("isCredentialsNonExpired 테스트")
        @Test
        void isCredentialsNonExpired() {
            assertFalse(userDetails.isCredentialsNonExpired());
        }

        @DisplayName("isEnabled 테스트")
        @Test
        void isEnabled() {
            assertFalse(userDetails.isEnabled());
        }

        @DisplayName("getName 테스트")
        @Test
        void getName() {
            assertNull(userDetails.getName());
        }

        @Nested
        @DisplayName("implies 테스트")
        class testImplies {
            @Test
            void implies_ShouldReturnTrue() {
                // Given
                Subject subject = new Subject();
                // When
                boolean result = userDetails.implies(subject);
                // Then
                assertFalse(result);
            }

            @Test
            void implies_ShouldReturnFalse() {
                // Given
                Subject subject = new Subject();
                // When
                boolean result = userDetails.implies(subject);
                // Then
                assertFalse(result);
            }
        }
    }
}
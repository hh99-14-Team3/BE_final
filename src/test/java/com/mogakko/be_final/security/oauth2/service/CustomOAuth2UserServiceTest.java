package com.mogakko.be_final.security.oauth2.service;

import com.mogakko.be_final.domain.members.entity.MemberStatusCode;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.entity.Role;
import com.mogakko.be_final.domain.members.entity.SocialType;
import com.mogakko.be_final.domain.members.repository.MembersRepository;
import com.mogakko.be_final.exception.CustomException;
import com.mogakko.be_final.security.oauth2.userinfo.OAuth2UserInfo;
import com.mogakko.be_final.security.oauth2.util.OAuthAttributes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.mogakko.be_final.exception.ErrorCode.DUPLICATE_IDENTIFIER;
import static com.mogakko.be_final.exception.ErrorCode.NOT_SUPPORTED_SOCIALTYPE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
class CustomOAuth2UserServiceTest {

    @Mock
    OAuth2UserInfo oauth2UserInfo;
    @Mock
    MembersRepository membersRepository;
    @Mock
    CustomOAuth2UserService oAuth2UserService;
    @InjectMocks
    CustomOAuth2UserService customOAuth2UserService;

    Members member = Members.builder()
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
            .codingTem(36.5)
            .build();

    @Nested
    @DisplayName("getSocialType Method 테스트")
    class GetSocialType {
        @DisplayName("getSocialType KAKAO 테스트")
        @Test
        void getSocialType_successWithKAKAO() {
            // given
            String registrationId = "kakao";

            // when
            SocialType socialType = customOAuth2UserService.getSocialType(registrationId);

            // then
            assertEquals(SocialType.KAKAO, socialType);
        }

        @DisplayName("getSocialType GOOGLE 테스트")
        @Test
        void getSocialType_successWithGOOGLE() {
            // given
            String registrationId = "google";

            // when
            SocialType socialType = customOAuth2UserService.getSocialType(registrationId);

            // then
            assertEquals(SocialType.GOOGLE, socialType);
        }

        @DisplayName("getSocialType GITHUB 테스트")
        @Test
        void getSocialType_successWithGITHUB() {
            // given
            String registrationId = "github";

            // when
            SocialType socialType = customOAuth2UserService.getSocialType(registrationId);

            // then
            assertEquals(SocialType.GITHUB, socialType);
        }

        @DisplayName("getSocialType 예외 테스트")
        @Test
        void getSocialType_fail() {
            // given
            String registrationId = "naver";

            // when
            CustomException customException = assertThrows(CustomException.class, () -> customOAuth2UserService.getSocialType(registrationId));

            // then
            assertEquals(NOT_SUPPORTED_SOCIALTYPE, customException.getErrorCode());
        }
    }

    @Nested
    @DisplayName("getMembers Method 테스트")
    class GetMembers {
        @DisplayName("getMembers 기존 유저 테스트")
        @Test
        void getMembers_successWithReturnMember() {
            // given
            OAuthAttributes oAuthAttributes = mock(OAuthAttributes.class);
            SocialType socialType = SocialType.GOOGLE;

            when(oAuthAttributes.getOauth2UserInfo()).thenReturn(oauth2UserInfo);
            when(oauth2UserInfo.getId()).thenReturn("id");
            when(membersRepository.findBySocialUidAndSocialType(any(), eq(socialType))).thenReturn(Optional.of(member));

            // when
            Members response = customOAuth2UserService.getMembers(oAuthAttributes, socialType);

            // then
            assertEquals(member, response);
        }
    }

    @Nested
    @DisplayName("saveMembers Method 테스트")
    class SaveMembers {
        @DisplayName("saveMembers 성공 테스트")
        @Test
        void saveMembers_success() {
            // given
            OAuthAttributes oAuthAttributes = mock(OAuthAttributes.class);
            SocialType socialType = SocialType.GOOGLE;

            when(oAuthAttributes.getOauth2UserInfo()).thenReturn(oauth2UserInfo);
            when(oauth2UserInfo.getEmail()).thenReturn("test@test.com");
            when(membersRepository.findByEmail(anyString())).thenReturn(Optional.empty());
            when(membersRepository.existsByFriendCode(anyInt())).thenReturn(false);
            when(oAuthAttributes.toEntity(eq(socialType), eq(oauth2UserInfo), anyInt())).thenReturn(member);
            when(membersRepository.existsByNickname(anyString())).thenReturn(false);
            when(membersRepository.save(member)).thenReturn(member);

            // when
            Members response = customOAuth2UserService.saveMembers(oAuthAttributes, socialType);

            // then
            assertEquals(member, response);
        }

        @DisplayName("saveMembers 중복 계정 테스트")
        @Test
        void saveMembers_failWithDuplicate() {
            // given
            OAuthAttributes oAuthAttributes = mock(OAuthAttributes.class);
            SocialType socialType = SocialType.GOOGLE;

            when(oAuthAttributes.getOauth2UserInfo()).thenReturn(oauth2UserInfo);
            when(oauth2UserInfo.getEmail()).thenReturn("test@test.com");
            when(membersRepository.findByEmail(anyString())).thenReturn(Optional.of(member));

            // when & then
            CustomException customException = assertThrows(CustomException.class, ()-> customOAuth2UserService.saveMembers(oAuthAttributes, socialType));
            assertEquals(DUPLICATE_IDENTIFIER, customException.getErrorCode());
        }

        @DisplayName("saveMembers do While 문 테스트")
        @Test
        void saveMembers_successWithDoWhile() {
            // given
            OAuthAttributes oAuthAttributes = mock(OAuthAttributes.class);
            SocialType socialType = SocialType.GOOGLE;

            when(oAuthAttributes.getOauth2UserInfo()).thenReturn(oauth2UserInfo);
            when(oauth2UserInfo.getEmail()).thenReturn("test@test.com");
            when(membersRepository.findByEmail(anyString())).thenReturn(Optional.empty());
            when(membersRepository.existsByFriendCode(anyInt())).thenReturn(true, false);
            when(oAuthAttributes.toEntity(eq(socialType), eq(oauth2UserInfo), anyInt())).thenReturn(member);
            when(membersRepository.existsByNickname(anyString())).thenReturn(false);
            when(membersRepository.save(member)).thenReturn(member);

            // when
            Members response = customOAuth2UserService.saveMembers(oAuthAttributes, socialType);

            // then
            assertEquals(member, response);
        }

        @DisplayName("saveMembers 중복 닉네임 테스트")
        @Test
        void saveMembers_successWithNicknameDuplication() {
            // given
            OAuthAttributes oAuthAttributes = mock(OAuthAttributes.class);
            SocialType socialType = SocialType.GOOGLE;

            when(oAuthAttributes.getOauth2UserInfo()).thenReturn(oauth2UserInfo);
            when(oauth2UserInfo.getEmail()).thenReturn("test@test.com");
            when(membersRepository.findByEmail(anyString())).thenReturn(Optional.empty());
            when(membersRepository.existsByFriendCode(anyInt())).thenReturn(true, false);
            when(oAuthAttributes.toEntity(eq(socialType), eq(oauth2UserInfo), anyInt())).thenReturn(member);
            when(membersRepository.existsByNickname(anyString())).thenReturn(true);
            when(membersRepository.save(member)).thenReturn(member);

            // when
            Members response = customOAuth2UserService.saveMembers(oAuthAttributes, socialType);

            // then
            assertEquals(member, response);
            assertNotEquals("nickname", response.getNickname());
        }
    }
}
package com.mogakko.be_final.security.oauth2.service;

import com.mogakko.be_final.domain.members.entity.SocialType;
import com.mogakko.be_final.domain.members.repository.MembersRepository;
import com.mogakko.be_final.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.mogakko.be_final.exception.ErrorCode.NOT_SUPPORTED_SOCIALTYPE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith({MockitoExtension.class})
class CustomOAuth2UserServiceTest {

    @Mock
    MembersRepository membersRepository;
    @InjectMocks
    CustomOAuth2UserService customOAuth2UserService;

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
            CustomException customException = assertThrows(CustomException.class, ()-> customOAuth2UserService.getSocialType(registrationId));

            // then
            assertEquals(NOT_SUPPORTED_SOCIALTYPE, customException.getErrorCode());
        }
    }

}
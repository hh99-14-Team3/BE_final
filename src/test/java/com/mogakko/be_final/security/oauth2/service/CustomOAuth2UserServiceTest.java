package com.mogakko.be_final.security.oauth2.service;

import com.mogakko.be_final.domain.members.entity.SocialType;
import com.mogakko.be_final.domain.members.repository.MembersRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    }

}
package com.mogakko.be_final.security.oauth2.util;

import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.entity.Role;
import com.mogakko.be_final.domain.members.entity.SocialType;
import com.mogakko.be_final.security.oauth2.userinfo.KakaoOAuth2UserInfo;
import com.mogakko.be_final.security.oauth2.userinfo.OAuth2UserInfo;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;

@Getter
public class OAuthAttributes {

    private String nameAttributeKey; // OAuth2 로그인 진행 시 키가 되는 필드 값, PK와 같은 의미
    private OAuth2UserInfo oauth2UserInfo; // 소셜 타입별 로그인 유저 정보(닉네임, 이메일, 프로필 사진 등등)


    @Builder
    public OAuthAttributes(String nameAttributeKey, OAuth2UserInfo oauth2UserInfo) {
        this.nameAttributeKey = nameAttributeKey;
        this.oauth2UserInfo = oauth2UserInfo;
    }
    public static OAuthAttributes of(SocialType socialType,
                                     String userNameAttributeName, Map<String, Object> attributes) {

        if (socialType == SocialType.KAKAO) {
            return ofKakao(userNameAttributeName, attributes);
        }else {
            throw new IllegalArgumentException("지원하지 않는 소셜 로그인입니다");
        }
    }

    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .oauth2UserInfo(new KakaoOAuth2UserInfo(attributes))
                .build();
    }
    public Members toEntity(SocialType socialType, OAuth2UserInfo oauth2UserInfo) {
        return Members.builder()
                .socialType(socialType)
                .socialUid(oauth2UserInfo.getId())
                .email(oauth2UserInfo.getEmail())
                .nickname(oauth2UserInfo.getNickname())
                .profileImage(oauth2UserInfo.getProfileImage())
                .role(Role.GUEST)
                .password(UUID.randomUUID().toString())
                .build();
    }
}

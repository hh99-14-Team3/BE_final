package com.mogakko.be_final.security.oauth2.util;

import com.mogakko.be_final.domain.members.entity.MemberStatusCode;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.entity.Role;
import com.mogakko.be_final.domain.members.entity.SocialType;
import com.mogakko.be_final.domain.members.service.MembersService;
import com.mogakko.be_final.security.oauth2.userinfo.GItHubOAuth2UserInfo;
import com.mogakko.be_final.security.oauth2.userinfo.GoogleOAuth2UserInfo;
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
        }
        else if (socialType == SocialType.GOOGLE) {
            return ofGoogle(userNameAttributeName, attributes);
        } else if (socialType == SocialType.GITHUB) {
            return ofGithub(userNameAttributeName, attributes);
        } else {
            throw new IllegalArgumentException("지원하지 않는 소셜 로그인입니다");
        }
    }

    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .oauth2UserInfo(new KakaoOAuth2UserInfo(attributes))
                .build();
    }

    public static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .oauth2UserInfo(new GoogleOAuth2UserInfo(attributes))
                .build();
    }

    public static OAuthAttributes ofGithub(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .oauth2UserInfo(new GItHubOAuth2UserInfo(attributes))
                .build();
    }

    //TODO: 이 부분에서 회원가입 시 필요한 정보를 입력 할 수 있음
    public Members toEntity(SocialType socialType, OAuth2UserInfo oauth2UserInfo, int friendCode) {
        return Members.builder()
                .socialType(socialType)
                .socialUid(oauth2UserInfo.getId())
                .email(oauth2UserInfo.getEmail())
                .nickname(oauth2UserInfo.getNickname())
                .profileImage(oauth2UserInfo.getProfileImage())
                .role(Role.GUEST)
                .codingTem(36.5)
                .mogakkoTotalTime(0L)
                .memberStatusCode(MemberStatusCode.BASIC)
                .githubId(oauth2UserInfo.getNickname())
                .friendCode(friendCode)
                .password(UUID.randomUUID().toString())
                .build();
    }
}

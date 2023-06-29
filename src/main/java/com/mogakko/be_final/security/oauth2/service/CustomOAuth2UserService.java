package com.mogakko.be_final.security.oauth2.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.entity.SocialType;
import com.mogakko.be_final.domain.members.repository.MembersRepository;
import com.mogakko.be_final.exception.CustomException;
import com.mogakko.be_final.exception.ErrorCode;
import com.mogakko.be_final.security.oauth2.util.CustomOAuth2User;
import com.mogakko.be_final.security.oauth2.util.OAuthAttributes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static com.mogakko.be_final.exception.ErrorCode.NOT_SUPPORTED_SOCIALTYPE;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MembersRepository membersRepository;
    private final RestTemplate restTemplate;

    private static final String KAKAO = "kakao";
    private static final String GOOGLE = "google";
    private static final String GITHUB = "github";

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("CustomOAuth2UserService.loadUser() 실행 - OAuth2 로그인 요청 진입");

        /**
         * DefaultOAuth2UserService 객체를 생성하여, loadUser(userRequest)를 통해 DefaultOAuth2User 객체를 생성 후 반환
         * DefaultOAuth2UserService의 loadUser()는 소셜 로그인 API의 사용자 정보 제공 URI로 요청을 보내서
         * 사용자 정보를 얻은 후, 이를 통해 DefaultOAuth2User 객체를 생성 후 반환한다.
         * 결과적으로, OAuth2User는 OAuth 서비스에서 가져온 유저 정보를 담고 있는 유저
         */
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        /**
         * userRequest에서 registrationId 추출 후 registrationId으로 SocialType 저장
         * http://localhost:8080/oauth2/authorization/kakao에서 kakao가 registrationId
         * userNameAttributeName은 이후에 nameAttributeKey로 설정된다.
         */
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        SocialType socialType = getSocialType(registrationId);
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName(); // OAuth2 로그인 시 키(PK)가 되는 값
        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes()); // 소셜 로그인에서 API가 제공하는 userInfo의 Json 값(유저 정보들)

        if (socialType.equals(SocialType.GITHUB)) {
            log.info("github 진입");
            String token = userRequest.getAccessToken().getTokenValue();

            // Create headers
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Send request to GitHub API
            ResponseEntity<JsonNode[]> response = restTemplate.exchange(
                    "https://api.github.com/user/emails", HttpMethod.GET, entity, JsonNode[].class);

            JsonNode primaryEmailNode = Arrays.stream(response.getBody())
                    .filter(e -> e.get("primary").asBoolean())
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No primary email found"));

            String email = primaryEmailNode.get("email").asText();

            if (email != null) {
                attributes.put("email", email);

            }
        }

        // socialType에 따라 유저 정보를 통해 OAuthAttributes 객체 생성
        OAuthAttributes extractAttributes = OAuthAttributes.of(socialType, userNameAttributeName, attributes);

        Members createdMembers = getMembers(extractAttributes, socialType); // getUser() 메소드로 User 객체 생성 후 반환

        // DefaultOAuth2User를 구현한 CustomOAuth2User 객체를 생성해서 반환
        return new CustomOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(createdMembers.getRole().getKey())),
                attributes,
                extractAttributes.getNameAttributeKey(),
                createdMembers.getEmail(),
                createdMembers.getRole()
        );
    }

    protected SocialType getSocialType(String registrationId) {

        if (KAKAO.equals(registrationId)) {
            return SocialType.KAKAO;
        } else if (GOOGLE.equals(registrationId)) {
            return SocialType.GOOGLE;
        } else if (GITHUB.equals(registrationId)) {
            return SocialType.GITHUB;
        } else {
            throw new CustomException(NOT_SUPPORTED_SOCIALTYPE);
        }
    }

    /**
     * SocialType과 attributes에 들어있는 소셜 로그인의 식별값 id를 통해 회원을 찾아 반환하는 메소드
     * 만약 찾은 회원이 있다면, 그대로 반환하고 없다면 saveUser()를 호출하여 회원을 저장한다.
     */
    protected Members getMembers(OAuthAttributes attributes, SocialType socialType) {
        Optional<Members> findUser = membersRepository.findBySocialUidAndSocialType(attributes.getOauth2UserInfo().getId(), socialType);
        return findUser.orElseGet(() -> saveMembers(attributes, socialType));
    }

    /**
     * OAuthAttributes의 toEntity() 메소드를 통해 빌더로 User 객체 생성 후 반환
     * 생성된 User 객체를 DB에 저장 : socialType, socialId, email, role 값만 있는 상태
     */
    protected Members saveMembers(OAuthAttributes attributes, SocialType socialType) {
        Optional<Members> existMember = membersRepository.findByEmail(attributes.getOauth2UserInfo().getEmail());
        if (existMember.isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE_IDENTIFIER);
        } else {
            int friendCode;
            do {
                friendCode = (int) ((Math.random() * ((999999 - 100000) + 1)) + 100000);
            } while (membersRepository.existsByFriendCode(friendCode));

            Members createdMembers = attributes.toEntity(socialType, attributes.getOauth2UserInfo(), friendCode);

            if (membersRepository.existsByNickname(createdMembers.getNickname())) {
                String temporaryNickname = createdMembers.getNickname() + "_" + Integer.toString(friendCode);
                createdMembers.updateNickname(temporaryNickname);
            }
            return membersRepository.save(createdMembers);
        }
    }
}
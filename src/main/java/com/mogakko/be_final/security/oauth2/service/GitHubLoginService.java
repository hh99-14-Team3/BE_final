package com.mogakko.be_final.security.oauth2.service;

import com.mogakko.be_final.domain.members.entity.Members;
import com.mogakko.be_final.domain.members.repository.MembersRepository;
import com.mogakko.be_final.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.mogakko.be_final.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class GitHubLoginService {

    private final RestTemplate restTemplate;
    private final MembersRepository membersRepository;


    @Value("${GITHUB_CLIENT_ID}")
    private String githubClientId;
    @Value("${SCOPE}")
    private String scope;
    @Value("${GITHUB_REDIRECT_URL}")
    private String redirectUrl;
    @Value("${GITHUB_CLIENT_SECRET}")
    private String githubClientSecret;


    public ResponseEntity<Map<String, String>> startGithubLogin(Members member) {
        String stateCode = UUID.randomUUID().toString();
        member.setGithubStateCode(stateCode);
        membersRepository.save(member);

        String url = "https://github.com/login/oauth/authorize?client_id=" + githubClientId
                + "&redirect_uri=" + redirectUrl
                + "&state=" + stateCode
                + "&scope=" + scope;

        Map<String, String> response = new HashMap<>();
        response.put("url", url);

        return ResponseEntity.ok(response);
    }

    public String requestAccessTokenFromGithub(String code) {
        String accessTokenUrl = "https://github.com/login/oauth/access_token";

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("client_id", githubClientId);
        parameters.add("client_secret", githubClientSecret);
        parameters.add("code", code);
        parameters.add("redirect_uri", redirectUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(parameters, headers);

        ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(accessTokenUrl, HttpMethod.POST, requestEntity, new ParameterizedTypeReference<Map<String, Object>>() {
        });

        Map<String, Object> map = responseEntity.getBody();
        if (responseEntity.getStatusCode() == HttpStatus.OK && map != null) {
            return (String) map.get("access_token");
        } else {
            throw new CustomException(GITHUB_TOKEN_REQUEST_ERROR);
        }
    }

    @Transactional
    public void getUserDetailsFromGithub(String state, String code) {
        String accessToken = requestAccessTokenFromGithub(code);
        String memberInfoUrl = "https://api.github.com/user";

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(memberInfoUrl, HttpMethod.GET, entity, new ParameterizedTypeReference<Map<String, Object>>() {
        });

        Map<String, Object> map = response.getBody();
        if (response.getStatusCode() == HttpStatus.OK && map != null) {
            Members member = membersRepository.findByGithubStateCode(state).orElseThrow(
                    () -> new CustomException(USER_NOT_FOUND)
            );
            member.setGithubId((String) map.get("login"));
            membersRepository.save(member);
        } else {
            throw new CustomException(FAILED_TO_GET_USERINFO);
        }
    }
}

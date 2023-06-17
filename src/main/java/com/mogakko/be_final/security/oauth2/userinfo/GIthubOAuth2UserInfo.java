package com.mogakko.be_final.security.oauth2.userinfo;

import java.util.Map;

public class GIthubOAuth2UserInfo extends OAuth2UserInfo{
    public GIthubOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }


    @Override
    public String getId() {
        Integer id = (int)attributes.get("id");
        System.out.println(attributes);
        return String.valueOf(id);
    }

    @Override
    public String getNickname() {
        return (String) attributes.get("login");
    }

    @Override
    public String getProfileImage() {
        return (String) attributes.get("avatar_url");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }
}

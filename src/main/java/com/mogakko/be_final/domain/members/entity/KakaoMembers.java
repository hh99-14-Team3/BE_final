package com.mogakko.be_final.domain.members.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KakaoMembers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long kakaoId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean isAgreed;


    public KakaoMembers( String email, String nickname, String password, boolean isAgreed){
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.isAgreed = isAgreed;

    }


    public KakaoMembers kakaoIdUpdate(Long kakaoId) {
        this.kakaoId = kakaoId;
        return this;
    }


}


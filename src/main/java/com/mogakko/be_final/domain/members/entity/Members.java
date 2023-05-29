package com.mogakko.be_final.domain.members.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mogakko.be_final.util.Timestamped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Members extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column
    private SocialType socialType;

    @Column
    @Lob
    private String profileImage;        //TODO : 디폴트 프사 생기면 url 넣을 예졍

    @Column
    private String socialUid;


    public Members(String email, String nickname, String password, Role role) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.role = role;
    }

    public void changePassword(String password) {
        this.password = password;
    }

    public void changeRole(Role newRole) {
        this.role = newRole;
    }

}

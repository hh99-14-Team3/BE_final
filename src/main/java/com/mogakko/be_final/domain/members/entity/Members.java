package com.mogakko.be_final.domain.members.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mogakko.be_final.domain.members.dto.request.GithubIdRequestDto;
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

    @Column
    private String githubId;

    @Enumerated(EnumType.STRING)
    @Column
    private SocialType socialType;

    // TODO : 디폴트 프사 url 임의로 지정해놓음
    @Column
    @Lob
    private String profileImage = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTtArY0iIz1b6rGdZ6xkSegyALtWQKBjupKJQ&usqp=CAU";

    @Column
    private String socialUid;

    @Enumerated(EnumType.STRING)
    @Column
    private MemberStatusCode memberStatusCode; // 상태코드

    @Column
    private Double codingTem = 36.5;

    @JsonIgnore
    @Column
    private Long mogakkoTotalTime = 0L;

    @JsonIgnore
    @Column
    private Long mogakkoWeekTime = 0L;



    public Members(String email, String nickname, String password, Role role, MemberStatusCode memberStatusCode) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.role = role;
        this.memberStatusCode = memberStatusCode;
    }

    public void changePassword(String password) {
        this.password = password;
    }

    public void changeRole(Role newRole) {
        this.role = newRole;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void deleteProfile() {
        this.profileImage = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTtArY0iIz1b6rGdZ6xkSegyALtWQKBjupKJQ&usqp=CAU";
    }

    public void setGithubId(String githubId) {
        this.githubId = githubId;
    }

    public void changeMemberStatusCode (MemberStatusCode memberStatusCode){
        this.memberStatusCode = memberStatusCode;
    }

    public void setTime(Long mogakkoTotalTime, Long mogakkoWeekTime){
        this.mogakkoTotalTime = mogakkoTotalTime;
        this.mogakkoWeekTime = mogakkoWeekTime;
    }
    public void setTime(Long mogakkoWeekTime){
        this.mogakkoWeekTime = mogakkoWeekTime;
    }

    public void addCodingTem(Double codingTem){
        this.codingTem = 36.5 + codingTem;
    }
}

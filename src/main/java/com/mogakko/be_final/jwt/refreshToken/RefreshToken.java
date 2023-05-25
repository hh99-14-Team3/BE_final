package com.mogakko.be_final.jwt.refreshToken;


import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Entity
@NoArgsConstructor
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String refreshToken;

    private String email;

    public RefreshToken(String tokenDto,  String email) {
        this.refreshToken = tokenDto;
        this.email = email;
    }

    public void updateToken(String tokenDto) {
        this.refreshToken = tokenDto;
    }

}
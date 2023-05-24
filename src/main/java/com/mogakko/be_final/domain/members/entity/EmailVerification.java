package com.mogakko.be_final.domain.members.entity;

import com.mogakko.be_final.util.Timestamped;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class EmailVerification extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String verificationKey;

    @Column(nullable = false)
    private LocalDateTime expirationTime; // timestamp indicating when the key will expire

    public EmailVerification(String email, String verificationKey, LocalDateTime expirationTime) {
        this.email = email;
        this.verificationKey = verificationKey;
        this.expirationTime = expirationTime;
    }

}

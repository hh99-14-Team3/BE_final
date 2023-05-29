package com.mogakko.be_final.domain.email.repository;

import com.mogakko.be_final.domain.email.entity.ConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, String> {
    Optional<ConfirmationToken> findByIdAndExpired(String confirmationTokenId, boolean expired);
}
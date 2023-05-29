package com.mogakko.be_final.domain.email.service;

import com.mogakko.be_final.domain.email.entity.ConfirmationToken;
import com.mogakko.be_final.domain.email.repository.ConfirmationTokenRepository;
import com.mogakko.be_final.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.mogakko.be_final.exception.ErrorCode.USED_TOKEN;

@RequiredArgsConstructor
@Service
public class ConfirmationTokenService {
    private final ConfirmationTokenRepository confirmationTokenRepository;

    public ConfirmationToken findByIdAndExpired(String confirmationTokenId) {
        Optional<ConfirmationToken> confirmationToken = confirmationTokenRepository.findByIdAndExpired(confirmationTokenId, false);
        return confirmationToken.orElseThrow(() -> new CustomException(USED_TOKEN));
    }
}

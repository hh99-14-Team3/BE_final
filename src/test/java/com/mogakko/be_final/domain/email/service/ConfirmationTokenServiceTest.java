package com.mogakko.be_final.domain.email.service;

import com.mogakko.be_final.domain.email.entity.ConfirmationToken;
import com.mogakko.be_final.domain.email.repository.ConfirmationTokenRepository;
import com.mogakko.be_final.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.mogakko.be_final.exception.ErrorCode.INVALID_TOKEN;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfirmationTokenServiceTest {

    @Mock
    ConfirmationTokenRepository confirmationTokenRepository;
    @InjectMocks
    ConfirmationTokenService confirmationTokenService;

    @Nested
    @DisplayName("findByIdAndExpired Method 테스트")
    class FindByIdAndExpired {
        @DisplayName("findByIdAndExpired 성공 테스트")
        @Test
        void findByIdAndExpired_success() {
            // given
            String confirmationTokenId = "confirmationTokenId";

            ConfirmationToken confirmationToken = ConfirmationToken.builder()
                    .email("test@test.com")
                    .build();

            when(confirmationTokenRepository.findByIdAndExpired(confirmationTokenId, false)).thenReturn(Optional.of(confirmationToken));

            // when
            ConfirmationToken response = confirmationTokenService.findByIdAndExpired(confirmationTokenId);

            // then
            assertEquals(confirmationToken, response);
        }

        @DisplayName("findByIdAndExpired 실패 테스트")
        @Test
        void findByIdAndExpired_fail() {
            // given
            String confirmationTokenId = "confirmationTokenId";

            when(confirmationTokenRepository.findByIdAndExpired(confirmationTokenId, false)).thenReturn(Optional.empty());

            // when & then
            CustomException customException = assertThrows(CustomException.class, ()-> confirmationTokenService.findByIdAndExpired(confirmationTokenId));
            assertEquals(INVALID_TOKEN, customException.getErrorCode());
        }
    }
}
package com.mogakko.be_final.domain.email.dto.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("EmailConfirmRequestDto 테스트")
@ExtendWith(MockitoExtension.class)
class EmailConfirmRequestDtoTest {

    @DisplayName("EmailConfirmRequestDto Getter 테스트")
    @Test
    void getEmail() {
        EmailConfirmRequestDto emailConfirmRequestDto = EmailConfirmRequestDto.builder().email("test@example.com").build();
        String email = emailConfirmRequestDto.getEmail();
        assertEquals("test@example.com", email);
    }

    @DisplayName("EmailConfirmRequestDto NoArgsConstructor 테스트")
    @Test
    void constructor() {
        EmailConfirmRequestDto emailConfirmRequestDto = new EmailConfirmRequestDto();
        String email = emailConfirmRequestDto.getEmail();
        assertNull(email);
    }
}
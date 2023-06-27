package com.mogakko.be_final.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;

import static com.mogakko.be_final.exception.ErrorCode.INTERNAL_SERER_ERROR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@DisplayName("Global Exception Handler 테스트")
@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    BindingResult bindingResult;
    @InjectMocks
    GlobalExceptionHandler globalExceptionHandler;

    @DisplayName("CustomException 클래스에서 발생하는 예외 핸들러 테스트")
    @Test
    void handleCustomException() {
        // Given
        CustomException exception = new CustomException(INTERNAL_SERER_ERROR);
        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleCustomException(exception);
        // Then
        assertEquals(exception.getErrorCode().getHttpStatus(), response.getStatusCode());
    }

}
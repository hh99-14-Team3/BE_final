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

    @DisplayName("Valid 예외 핸들러 테스트")
    @Test
    void handleBindException() {
        // Given
        BindException bindException = new BindException(bindingResult);
        String errorMessage = "Validation failed.";
        when(bindingResult.getFieldErrors()).thenReturn(List.of(new FieldError("", "", errorMessage)));

        // When
        ResponseEntity<ErrorResponse> responseEntity = globalExceptionHandler.handleBindException(bindException);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        ErrorResponse errorResponse = responseEntity.getBody();
        Assertions.assertNotNull(errorResponse);
        assertEquals(errorMessage, errorResponse.getMessage());
    }

    @DisplayName("예외 핸들러 테스트")
    @Test
    void handleException() {
        // Given
        Exception exception = new Exception("Internal Server Error");

        // When
        ResponseEntity<ErrorResponse> responseEntity = globalExceptionHandler.handleException(exception);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        HttpStatus errorResponse = responseEntity.getStatusCode();
        Assertions.assertNotNull(errorResponse);
        assertEquals(exception.getMessage(), errorResponse.getReasonPhrase());
    }

    @DisplayName("런타임 예외 핸들러 테스트")
    @Test
    void handleRuntimeException() {
        // Given
        RuntimeException runtimeException = new RuntimeException("Internal Server Error");
        // When
        ResponseEntity<ErrorResponse> responseEntity = globalExceptionHandler.handleRuntimeException(runtimeException);
        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        HttpStatus errorResponse = responseEntity.getStatusCode();
        Assertions.assertNotNull(errorResponse);
        assertEquals(runtimeException.getMessage(), errorResponse.getReasonPhrase());
    }
}
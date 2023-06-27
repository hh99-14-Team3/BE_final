package com.mogakko.be_final.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("ErrorResponse Test")
@ExtendWith(MockitoExtension.class)
class ErrorResponseTest {

    @DisplayName("에러 반환 형식 테스트 - Error Code")
    @Test
    void toResponseEntity() {
        // Given
        ErrorCode errorCode = ErrorCode.NOT_FOUND;
        // When
        ResponseEntity<ErrorResponse> responseEntity = ErrorResponse.toResponseEntity(errorCode);
        // Then
        assertEquals(errorCode.getHttpStatus(), responseEntity.getStatusCode());
        assertEquals(errorCode.getData(), responseEntity.getBody().getMessage());
    }

    @DisplayName("에러 반환 형식 테스트 - Error Code, HttpStatus")
    @Test
    void toResponseEntityValid() {
        // Given
        String errorCode = "ErrorCode";
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        // When
        ResponseEntity<ErrorResponse> responseEntity = ErrorResponse.toResponseEntityValid(errorCode, httpStatus);
        // Then
        assertEquals(errorCode, responseEntity.getBody().getMessage());
        assertEquals(httpStatus, responseEntity.getStatusCode());
    }

    @DisplayName("에러 반환 형식 테스트 - HttpStatus, data")
    @Test
    void testToResponseEntity() {
        // Given
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        String data = "data";
        // When
        ResponseEntity<ErrorResponse> responseEntity = ErrorResponse.toResponseEntity(httpStatus, data);
        // Then
        assertEquals(httpStatus, responseEntity.getStatusCode());
        assertEquals(data, responseEntity.getBody().getData());
    }
}
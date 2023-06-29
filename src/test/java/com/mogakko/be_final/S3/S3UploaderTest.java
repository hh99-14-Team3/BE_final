package com.mogakko.be_final.S3;

import com.amazonaws.services.s3.AmazonS3;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("S3 Uploader 테스트")
@ExtendWith(MockitoExtension.class)
public class S3UploaderTest {
    @Mock
    private AmazonS3 amazonS3;

    private S3Uploader s3Uploader;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        s3Uploader = new S3Uploader(amazonS3);
    }

    @DisplayName("파일 업로드 성공 테스트")
    @Test
    public void uploadFile() throws IOException {
        // Given
        when(amazonS3.putObject(any(), any(), any(InputStream.class), any())).thenReturn(null);
        URL s3ObjectUrl = new URL("https://example.com/test-file");
        when(amazonS3.getUrl(any(), any())).thenReturn(s3ObjectUrl);

        // When
        MultipartFile multipartFile = new MockMultipartFile("testfile.txt", "Test file content".getBytes());
        String url = s3Uploader.uploadFile(multipartFile);

        // Then
        assertEquals(s3ObjectUrl.toString(), url);
    }

    @DisplayName("파일 확장자 없는 경우 처리 테스트 - AssertionError")
    @Test
    void getFileExtension_NullOriginalFileName() {
        // Given
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn(null);

        // When
        AssertionError assertionError = assertThrows(AssertionError.class, () -> s3Uploader.getFileExtension(file));

        // Then
        assertEquals("java.lang.AssertionError", assertionError.toString());
    }

    @DisplayName("파일 확장자가 없는 경우 처리 테스트")
    @Test
    void getFileExtension_NoExtension() {
        // given
        MultipartFile file = mock(MultipartFile.class);
        String originalFileName = "file";
        when(file.getOriginalFilename()).thenReturn(originalFileName);

        // when
        String extension = s3Uploader.getFileExtension(file);

        // then
        assertEquals("", extension);
    }

    @DisplayName("파일 확장자가 있는 경우 처리 테스트")
    @Test
    void getFileExtension_WithExtension() {
        // Given
        MultipartFile file = mock(MultipartFile.class);
        String originalFileName = "file.jpg";
        when(file.getOriginalFilename()).thenReturn(originalFileName);
        // When
        String extension = s3Uploader.getFileExtension(file);
        // Then
        assertEquals(".jpg", extension);
    }

}

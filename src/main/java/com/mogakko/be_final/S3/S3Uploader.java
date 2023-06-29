package com.mogakko.be_final.S3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class S3Uploader {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadFile(MultipartFile multipartFile) throws IOException {
        String fileExtension = getFileExtension(multipartFile);
        String fileName = UUID.randomUUID() + "_" + multipartFile.getOriginalFilename() + fileExtension;
        ObjectMetadata objMeta = new ObjectMetadata();
        objMeta.setContentLength(multipartFile.getSize());
        objMeta.setContentType(getFileContentType(multipartFile));

        amazonS3.putObject(bucket, fileName, multipartFile.getInputStream(), objMeta);

        return amazonS3.getUrl(bucket, fileName).toString();
    }

    public void delete(String fileUrl) {
        String[] temp = fileUrl.split("/");
        String fileKey = temp[temp.length - 1];
        amazonS3.deleteObject(bucket, fileKey);
    }

    String getFileExtension(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();
        assert originalFileName != null;
        int dotIndex = originalFileName.lastIndexOf(".");
        return (dotIndex == -1) ? "" : originalFileName.substring(dotIndex);
    }

    private String getFileContentType(MultipartFile file) {
        return file.getContentType();
    }
}

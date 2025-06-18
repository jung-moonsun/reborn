package com.ms.reborn.domain.file.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String region;

    private S3Client s3Client;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final List<String> ALLOWED_EXT = List.of(".jpg", ".jpeg", ".png", ".gif");

    @PostConstruct
    public void initializeS3() {
        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)
                ))
                .build();
    }

    public String storeFile(MultipartFile file, String category) {
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";

        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        }

        if (!ALLOWED_EXT.contains(fileExtension)) {
            throw new IllegalArgumentException("허용되지 않은 파일 형식입니다.");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("최대 5MB까지 업로드할 수 있습니다.");
        }

        String fileName = category + "/" + UUID.randomUUID() + fileExtension;

        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(fileName)
                            .contentType(file.getContentType())
//                            .acl("public-read")
                            .build(),
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );

            return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("S3 업로드 실패", e);
        }
    }
}

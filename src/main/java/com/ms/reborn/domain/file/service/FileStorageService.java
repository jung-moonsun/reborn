package com.ms.reborn.domain.file.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final List<String> ALLOWED_EXT = List.of(".jpg", ".jpeg", ".png", ".gif");

    public FileStorageService(@Value("${file.upload-dir}") String uploadDir) {
        this.fileStorageLocation = Paths.get(System.getProperty("user.dir"))
                .resolve(uploadDir)
                .toAbsolutePath()
                .normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 폴더 생성 실패", e);
        }
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

        String fileName = UUID.randomUUID().toString() + fileExtension;

        try {
            Path categoryPath = this.fileStorageLocation.resolve(category);
            if (!Files.exists(categoryPath)) {
                Files.createDirectories(categoryPath);
            }

            Path targetLocation = categoryPath.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // 클라이언트에서는 "/uploads/{category}/{파일명}" 으로 접근
            return "/uploads/" + category + "/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패", e);
        }
    }
}

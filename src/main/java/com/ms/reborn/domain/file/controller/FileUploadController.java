package com.ms.reborn.domain.file.controller;

import com.ms.reborn.domain.file.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class FileUploadController {

    private final FileStorageService fileStorageService;

    @Operation(
            summary = "파일 업로드",
            description = "채팅 관련 이미지/파일을 업로드하고 URL을 반환합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(mediaType = "multipart/form-data")
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "업로드 성공"),
                    @ApiResponse(responseCode = "400", description = "파일 오류 (확장자, 용량 등)")
            }
    )
    @PostMapping(value = "/upload", consumes = "multipart/form-data", produces = "application/json")
    public ResponseEntity<Map<String, String>> uploadFile(
            @Parameter(description = "업로드할 파일", required = true)
            @RequestPart("file") MultipartFile file,
            @Parameter(description = "카테고리 (기본값: chat)")
            @RequestParam(value = "category", defaultValue = "chat") String category) {

        String fileUrl = fileStorageService.storeFile(file, category);

        Map<String, String> result = new HashMap<>();
        result.put("fileUrl", fileUrl);
        return ResponseEntity.ok(result);
    }
}

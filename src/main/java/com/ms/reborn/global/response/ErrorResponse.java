package com.ms.reborn.global.response;

import com.ms.reborn.global.exception.ErrorCode;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ErrorResponse {
    private boolean success;
    private int status;
    private int code;
    private String error;
    private String message;
    private String path;
    private LocalDateTime timestamp;
    private Map<String, String> validation;  // Field validation errors

    public static ErrorResponse of(ErrorCode errorCode, String path) {
        return ErrorResponse.builder()
                .success(false)
                .status(errorCode.getHttpStatus().value())
                .code(errorCode.getCode())
                .error(errorCode.name())
                .message(errorCode.getMessage())
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
// src/main/java/com/ms/reborn/global/exception/GlobalExceptionHandler.java
package com.ms.reborn.global.exception;

import com.ms.reborn.global.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ... existing handlers ...

    /**
     * 서비스 레이어에서 던진 IllegalArgumentException을
     * 400 Bad Request로 매핑
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest req
    ) {
        ErrorResponse base = ErrorResponse.of(ErrorCode.INVALID_INPUT, req.getRequestURI());
        ErrorResponse body = ErrorResponse.builder()
                .success(false)
                .status(base.getStatus())
                .code(base.getCode())
                .error(base.getError())
                .message(ex.getMessage())   // 서비스에서 던진 메시지
                .path(base.getPath())
                .timestamp(base.getTimestamp())
                .build();
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustom(CustomException ex, HttpServletRequest req) {
        ErrorResponse error = ErrorResponse.of(ex.getErrorCode(), req.getRequestURI());
        return ResponseEntity.status(ex.getErrorCode().getHttpStatus())
                .body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception ex, HttpServletRequest req) {
        ErrorResponse error = ErrorResponse.of(ErrorCode.INTERNAL_ERROR, req.getRequestURI());
        return ResponseEntity.status(error.getStatus()).body(error);
    }
}

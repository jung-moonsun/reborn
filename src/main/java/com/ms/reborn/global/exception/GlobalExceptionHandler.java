package com.ms.reborn.global.exception;

import com.ms.reborn.global.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException; // ✅ 추가
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * ✅ @Valid 검증 실패 (Field Validation)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        ErrorResponse body = ErrorResponse.builder()
                .success(false)
                .status(400)
                .code(1000)
                .error("VALIDATION_ERROR")
                .message("요청 데이터 검증 실패")
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .validation(errors)
                .build();

        return ResponseEntity.badRequest().body(body);
    }

    /**
     * 서비스 레이어에서 던진 IllegalArgumentException → 400 Bad Request
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
                .message(ex.getMessage())
                .path(base.getPath())
                .timestamp(base.getTimestamp())
                .build();
        return ResponseEntity.badRequest().body(body);
    }

    /**
     * 커스텀 예외 처리 (ErrorCode 기반)
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustom(CustomException ex, HttpServletRequest req) {
        ErrorResponse error = ErrorResponse.of(ex.getErrorCode(), req.getRequestURI());
        return ResponseEntity.status(ex.getErrorCode().getHttpStatus()).body(error);
    }

    /**
     * 나머지 예외 → 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception ex, HttpServletRequest req) {
        ErrorResponse error = ErrorResponse.of(ErrorCode.INTERNAL_ERROR, req.getRequestURI());
        return ResponseEntity.status(error.getStatus()).body(error);
    }
}

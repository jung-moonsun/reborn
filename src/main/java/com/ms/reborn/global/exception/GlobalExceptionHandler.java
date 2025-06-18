package com.ms.reborn.global.exception;

import com.ms.reborn.global.response.ApiResponse;
import com.ms.reborn.global.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        String msg = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        // 공통 INVALID_INPUT 사용
        ErrorResponse error = ErrorResponse.of(ErrorCode.INVALID_INPUT, req.getRequestURI());
        return ResponseEntity.badRequest().body(
                ErrorResponse.builder()
                        .success(false)
                        .status(error.getStatus())
                        .code(error.getCode())
                        .error(error.getError())
                        .message(msg)
                        .path(error.getPath())
                        .timestamp(error.getTimestamp())
                        .build()
        );
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

package com.ms.reborn.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."),
    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "이미 존재하는 이메일입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."),
    // 추가 필요시 계속 정의
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "입력값 오류"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다."),
    FILE_UPLOAD_ERROR(HttpStatus.BAD_REQUEST, "파일 업로드 실패");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.httpStatus = status;
        this.message = message;
    }
}

package com.ms.reborn.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // 사용자
    USER_NOT_FOUND(1001, HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."),
    DUPLICATE_EMAIL(1002, HttpStatus.BAD_REQUEST, "이미 존재하는 이메일입니다."),
    INVALID_PASSWORD(1003, HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    UNAUTHORIZED_USER(1004, HttpStatus.UNAUTHORIZED, "권한이 없습니다."),

    // 상품
    PRODUCT_NOT_FOUND(2001, HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다."),

    // 댓글
    COMMENT_NOT_FOUND(5001, HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."),

    // 채팅
    CHAT_ROOM_NOT_FOUND(3001, HttpStatus.NOT_FOUND, "채팅방이 존재하지 않습니다."),
    CHAT_MESSAGE_NOT_FOUND(3002, HttpStatus.NOT_FOUND, "메시지를 찾을 수 없습니다."),
    CHAT_ACCESS_DENIED(3003, HttpStatus.FORBIDDEN, "채팅방에 접근할 권한이 없습니다."),
    EMPTY_MESSAGE(3004, HttpStatus.BAD_REQUEST, "메시지 내용이 비어 있습니다."),
    IMAGE_MESSAGE_NEEDS_URL(3005, HttpStatus.BAD_REQUEST, "이미지 메시지는 fileUrl이 필요합니다."),


    // 파일
    FILE_UPLOAD_ERROR(4001, HttpStatus.BAD_REQUEST, "파일 업로드 실패"),

    // 공통
    INVALID_INPUT(9001, HttpStatus.BAD_REQUEST, "입력값 오류"),
    INTERNAL_ERROR(9999, HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류")
    ;

    private final int code;
    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(int code, HttpStatus httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }
}

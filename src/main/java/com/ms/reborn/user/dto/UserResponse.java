package com.ms.reborn.user.dto;

import lombok.Getter;

@Getter
public class UserResponse {

    private Long id;
    private String email;
    private String nickname;

    public UserResponse(Long id, String email, String nickname) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
    }
}

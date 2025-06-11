package com.ms.reborn.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequest {
    @Email(message = "유효한 이메일 입력")
    @NotBlank(message = "이메일 필수")
    private String email;

    @NotBlank(message = "비밀번호 필수")
    @Size(min = 6, message = "비밀번호는 6자 이상")
    private String password;

    @NotBlank(message = "닉네임 필수")
    private String nickname;
}

// src/main/java/com/ms/reborn/domain/user/dto/UserRequest.java
package com.ms.reborn.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequest {

    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    @Pattern(
            regexp  = "^[\\w.%+-]+@[\\w.-]+\\.com$",
            message = ".com 도메인만 허용됩니다."
    )
    private String email;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
    @Pattern(regexp = ".*[A-Z].*", message = "비밀번호에 대문자를 하나 이상 포함해야 합니다.")
    @Pattern(regexp = ".*[a-z].*", message = "비밀번호에 소문자를 하나 이상 포함해야 합니다.")
    @Pattern(regexp = ".*\\d.*",   message = "비밀번호에 숫자를 하나 이상 포함해야 합니다.")
    @Pattern(regexp = ".*[^\\w\\s].*", message = "비밀번호에 특수문자를 하나 이상 포함해야 합니다.")
    private String password;

    @NotBlank(message = "닉네임을 입력해주세요.")
    @Size(min = 2, max = 30, message = "닉네임은 2~30자 사이여야 합니다.")
    @Pattern(
            regexp  = "^[가-힣a-zA-Z0-9]+$",
            message = "닉네임에 특수문자를 사용할 수 없습니다."
    )
    private String nickname;
}

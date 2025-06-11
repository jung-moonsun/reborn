package com.ms.reborn.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequest {
    @NotBlank(message = "닉네임 필수")
    private String nickname;
}

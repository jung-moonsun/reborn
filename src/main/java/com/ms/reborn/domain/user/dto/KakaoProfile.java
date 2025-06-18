package com.ms.reborn.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class KakaoProfile {
    private Long id;
    @JsonProperty("kakao_account")
    private KakaoAccount kakao_account;

    @Data
    public static class KakaoAccount {
        private Profile profile;
        private String email;
    }

    @Data
    public static class Profile {
        private String nickname;
    }
}

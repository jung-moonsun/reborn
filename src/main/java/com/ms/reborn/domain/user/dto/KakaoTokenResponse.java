package com.ms.reborn.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class KakaoTokenResponse {
    @JsonProperty("access_token")
    private String access_token;
    @JsonProperty("token_type")
    private String token_type;
    @JsonProperty("refresh_token")
    private String refresh_token;
    @JsonProperty("expires_in")
    private Long expires_in;
    @JsonProperty("scope")
    private String scope;
}

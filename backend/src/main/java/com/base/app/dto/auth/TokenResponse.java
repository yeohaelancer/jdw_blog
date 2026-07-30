package com.base.app.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenResponse {
    private String accessToken;
    private String refreshToken;
    private Long userId;
    private String nickname;
    private Long blogId;
    private String role;
}

package com.base.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {

    private String secret;
    private long accessTokenExpMinutes;
    private long refreshTokenExpDays;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getAccessTokenExpMinutes() {
        return accessTokenExpMinutes;
    }

    public void setAccessTokenExpMinutes(long accessTokenExpMinutes) {
        this.accessTokenExpMinutes = accessTokenExpMinutes;
    }

    public long getRefreshTokenExpDays() {
        return refreshTokenExpDays;
    }

    public void setRefreshTokenExpDays(long refreshTokenExpDays) {
        this.refreshTokenExpDays = refreshTokenExpDays;
    }
}

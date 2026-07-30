package com.base.app.controller;

import com.base.app.config.AuthContext;
import com.base.app.dto.ApiResponse;
import com.base.app.dto.UserDto;
import com.base.app.dto.auth.LoginRequest;
import com.base.app.dto.auth.RefreshRequest;
import com.base.app.dto.auth.SignupRequest;
import com.base.app.dto.auth.TokenResponse;
import com.base.app.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Auth", description = "회원가입/로그인/토큰 재발급")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "이메일 중복 확인")
    @GetMapping("/check-email")
    public ApiResponse<Map<String, Boolean>> checkEmail(@RequestParam String email) {
        return ApiResponse.success(Map.of("available", authService.checkEmailAvailable(email)));
    }

    @Operation(summary = "회원가입 (블로그 자동 개설)")
    @PostMapping("/signup")
    public ApiResponse<TokenResponse> signup(@Valid @RequestBody SignupRequest request) {
        return ApiResponse.success("회원가입이 완료되었습니다.", authService.signup(request));
    }

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public ApiResponse<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    @Operation(summary = "Access Token 재발급")
    @PostMapping("/refresh")
    public ApiResponse<TokenResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        return ApiResponse.success(authService.refresh(request.getRefreshToken()));
    }

    @Operation(summary = "내 정보 조회")
    @GetMapping("/me")
    public ApiResponse<UserDto> me() {
        UserDto user = authService.getUser(AuthContext.requireUserId());
        user.setPassword(null);
        return ApiResponse.success(user);
    }
}

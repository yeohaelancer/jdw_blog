package com.base.app.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private static final String HEADER = "Authorization";
    private static final String PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;

    /**
     * GET 요청은 비로그인 열람(공개 글 목록/상세)을 허용하므로 토큰이 있으면만 파싱(optional auth).
     * GET 이외(POST/PUT/DELETE 등)는 토큰이 반드시 유효해야 함.
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String header = request.getHeader(HEADER);
        String token = (header != null && header.startsWith(PREFIX)) ? header.substring(PREFIX.length()) : null;
        boolean validAccessToken = token != null && jwtTokenProvider.validate(token) && !jwtTokenProvider.isRefreshToken(token);

        if (validAccessToken) {
            AuthContext.setUserId(jwtTokenProvider.getUserId(token));
            return true;
        }

        if ("GET".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        throw new UnauthorizedException("유효하지 않거나 만료된 인증 토큰입니다.");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        AuthContext.clear();
    }
}

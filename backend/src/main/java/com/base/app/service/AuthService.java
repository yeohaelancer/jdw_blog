package com.base.app.service;

import com.base.app.config.JwtTokenProvider;
import com.base.app.config.UnauthorizedException;
import com.base.app.dto.BlogDto;
import com.base.app.dto.UserDto;
import com.base.app.dto.auth.LoginRequest;
import com.base.app.dto.auth.SignupRequest;
import com.base.app.dto.auth.TokenResponse;
import com.base.app.mapper.BlogMapper;
import com.base.app.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private static final Pattern NON_ALNUM = Pattern.compile("[^a-z0-9]+");

    private final UserMapper userMapper;
    private final BlogMapper blogMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public boolean checkEmailAvailable(String email) {
        return !userMapper.existsByEmail(email);
    }

    @Transactional
    public TokenResponse signup(SignupRequest request) {
        if (userMapper.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        UserDto user = new UserDto();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        userMapper.insert(user);
        user.setRole("USER");

        BlogDto blog = new BlogDto();
        blog.setUserId(user.getId());
        blog.setBlogName(request.getNickname() + "의 블로그");
        blog.setBlogUrlSlug(generateUniqueSlug(request.getNickname()));
        blogMapper.insert(blog);

        return issueTokens(user, blog.getId());
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {
        UserDto user = userMapper.findByEmail(request.getEmail());
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }
        if (!"ACTIVE".equals(user.getStatus())) {
            throw new UnauthorizedException("이용이 제한된 계정입니다.");
        }
        BlogDto blog = blogMapper.findByUserId(user.getId());
        return issueTokens(user, blog != null ? blog.getId() : null);
    }

    @Transactional
    public TokenResponse refresh(String refreshToken) {
        if (!jwtTokenProvider.validate(refreshToken) || !jwtTokenProvider.isRefreshToken(refreshToken)) {
            throw new UnauthorizedException("유효하지 않은 리프레시 토큰입니다.");
        }
        Long userId = jwtTokenProvider.getUserId(refreshToken);
        UserDto user = userMapper.findById(userId);
        if (user == null) {
            throw new UnauthorizedException("존재하지 않는 사용자입니다.");
        }
        BlogDto blog = blogMapper.findByUserId(user.getId());
        return issueTokens(user, blog != null ? blog.getId() : null);
    }

    public UserDto getUser(Long userId) {
        UserDto user = userMapper.findById(userId);
        if (user == null) {
            throw new UnauthorizedException("존재하지 않는 사용자입니다.");
        }
        return user;
    }

    private TokenResponse issueTokens(UserDto user, Long blogId) {
        String accessToken = jwtTokenProvider.createAccessToken(user.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());
        return new TokenResponse(accessToken, refreshToken, user.getId(), user.getNickname(), blogId, user.getRole());
    }

    private String generateUniqueSlug(String nickname) {
        String base = NON_ALNUM.matcher(nickname.toLowerCase(Locale.ROOT)).replaceAll("");
        if (base.isBlank()) {
            base = "blog";
        }
        return base + "-" + UUID.randomUUID().toString().substring(0, 8);
    }
}

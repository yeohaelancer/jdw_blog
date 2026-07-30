package com.base.app.service;

import com.base.app.config.AuthContext;
import com.base.app.dto.UserDto;
import com.base.app.dto.admin.AdminPostSummary;
import com.base.app.exception.ForbiddenException;
import com.base.app.exception.NotFoundException;
import com.base.app.mapper.CategoryMapper;
import com.base.app.mapper.PostMapper;
import com.base.app.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final UserMapper userMapper;
    private final PostMapper postMapper;
    private final CategoryMapper categoryMapper;

    public List<UserDto> listUsers() {
        requireAdmin();
        return userMapper.findAll();
    }

    @Transactional
    public void updateUserStatus(Long targetUserId, String status) {
        requireAdmin();
        if (!List.of("ACTIVE", "SUSPENDED", "WITHDRAWN").contains(status)) {
            throw new IllegalArgumentException("유효하지 않은 상태 값입니다.");
        }
        UserDto target = userMapper.findById(targetUserId);
        if (target == null) {
            throw new NotFoundException("회원을 찾을 수 없습니다.");
        }
        userMapper.updateStatus(targetUserId, status);
    }

    @Transactional
    public void updateUserRole(Long targetUserId, String role) {
        Long adminUserId = requireAdmin();
        if (!List.of("USER", "ADMIN").contains(role)) {
            throw new IllegalArgumentException("유효하지 않은 권한 값입니다.");
        }
        if (targetUserId.equals(adminUserId) && "USER".equals(role)) {
            throw new IllegalArgumentException("본인의 관리자 권한은 스스로 해제할 수 없습니다.");
        }
        UserDto target = userMapper.findById(targetUserId);
        if (target == null) {
            throw new NotFoundException("회원을 찾을 수 없습니다.");
        }
        userMapper.updateRole(targetUserId, role);
    }

    public List<AdminPostSummary> listPosts() {
        requireAdmin();
        return postMapper.findAllForAdmin();
    }

    @Transactional
    public void deletePost(Long postId) {
        requireAdmin();
        Long ownerUserId = postMapper.findOwnerUserId(postId);
        if (ownerUserId == null) {
            throw new NotFoundException("게시글을 찾을 수 없습니다.");
        }
        var post = postMapper.findDetailById(postId);
        postMapper.softDelete(postId);
        if (post != null && post.getCategoryId() != null) {
            categoryMapper.adjustPostCount(post.getCategoryId(), -1);
        }
    }

    private Long requireAdmin() {
        Long userId = AuthContext.requireUserId();
        UserDto user = userMapper.findById(userId);
        if (user == null || !"ADMIN".equals(user.getRole())) {
            throw new ForbiddenException("관리자 권한이 필요합니다.");
        }
        return userId;
    }
}

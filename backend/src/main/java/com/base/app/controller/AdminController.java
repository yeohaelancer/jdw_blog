package com.base.app.controller;

import com.base.app.dto.ApiResponse;
import com.base.app.dto.UserDto;
import com.base.app.dto.admin.AdminPostSummary;
import com.base.app.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Admin", description = "사이트 전체 관리 (회원/게시글) - ADMIN 권한 전용")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "전체 회원 목록")
    @GetMapping("/users")
    public ApiResponse<List<UserDto>> listUsers() {
        List<UserDto> users = adminService.listUsers();
        users.forEach(u -> u.setPassword(null));
        return ApiResponse.success(users);
    }

    @Operation(summary = "회원 상태 변경 (ACTIVE/SUSPENDED/WITHDRAWN)")
    @PatchMapping("/users/{userId}/status")
    public ApiResponse<Void> updateUserStatus(@PathVariable Long userId, @RequestBody StatusRequest request) {
        adminService.updateUserStatus(userId, request.status);
        return ApiResponse.success("회원 상태가 변경되었습니다.", null);
    }

    @Operation(summary = "회원 권한 변경 (USER/ADMIN)")
    @PatchMapping("/users/{userId}/role")
    public ApiResponse<Void> updateUserRole(@PathVariable Long userId, @RequestBody RoleRequest request) {
        adminService.updateUserRole(userId, request.role);
        return ApiResponse.success("회원 권한이 변경되었습니다.", null);
    }

    @Operation(summary = "전체 게시글 목록 (공개범위/상태 무관)")
    @GetMapping("/posts")
    public ApiResponse<List<AdminPostSummary>> listPosts() {
        return ApiResponse.success(adminService.listPosts());
    }

    @Operation(summary = "게시글 강제 삭제 (소유자 무관)")
    @DeleteMapping("/posts/{postId}")
    public ApiResponse<Void> deletePost(@PathVariable Long postId) {
        adminService.deletePost(postId);
        return ApiResponse.success("게시글이 삭제되었습니다.", null);
    }

    public static class StatusRequest {
        @NotBlank
        public String status;
    }

    public static class RoleRequest {
        @NotBlank
        public String role;
    }
}

package com.base.app.controller;

import com.base.app.dto.ApiResponse;
import com.base.app.dto.NeighborBlogResponse;
import com.base.app.service.NeighborService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Neighbor", description = "이웃/서로이웃 관리")
@RestController
@RequiredArgsConstructor
public class NeighborController {

    private final NeighborService neighborService;

    @Operation(summary = "내 이웃 목록")
    @GetMapping("/api/neighbors/mine")
    public ApiResponse<List<NeighborBlogResponse>> myNeighbors() {
        return ApiResponse.success(neighborService.myList());
    }

    @Operation(summary = "특정 블로그와의 이웃 상태 조회 (null=이웃 아님, ONE_SIDED, MUTUAL)")
    @GetMapping("/api/blogs/{blogId}/neighbor-status")
    public ApiResponse<Map<String, String>> status(@PathVariable Long blogId) {
        String type = neighborService.status(blogId);
        Map<String, String> body = new java.util.HashMap<>();
        body.put("type", type);
        return ApiResponse.success(body);
    }

    @Operation(summary = "이웃 추가")
    @PostMapping("/api/blogs/{blogId}/neighbors")
    public ApiResponse<Void> add(@PathVariable Long blogId) {
        neighborService.add(blogId);
        return ApiResponse.success("이웃으로 추가되었습니다.", null);
    }

    @Operation(summary = "이웃 삭제")
    @DeleteMapping("/api/blogs/{blogId}/neighbors")
    public ApiResponse<Void> remove(@PathVariable Long blogId) {
        neighborService.remove(blogId);
        return ApiResponse.success("이웃이 삭제되었습니다.", null);
    }
}

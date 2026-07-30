package com.base.app.controller;

import com.base.app.dto.ApiResponse;
import com.base.app.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Like", description = "게시글 공감(좋아요) 토글")
@RestController
@RequestMapping("/api/posts/{postId}/like")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @Operation(summary = "좋아요 토글")
    @PostMapping
    public ApiResponse<Map<String, Boolean>> toggle(@PathVariable Long postId) {
        boolean liked = likeService.toggle(postId);
        return ApiResponse.success(Map.of("liked", liked));
    }
}

package com.base.app.controller;

import com.base.app.dto.ApiResponse;
import com.base.app.dto.comment.CommentCreateRequest;
import com.base.app.dto.comment.CommentResponse;
import com.base.app.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Comment", description = "댓글/대댓글")
@RestController
@RequestMapping("/api/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "댓글 목록")
    @GetMapping
    public ApiResponse<List<CommentResponse>> list(@PathVariable Long postId) {
        return ApiResponse.success(commentService.list(postId));
    }

    @Operation(summary = "댓글/대댓글 작성")
    @PostMapping
    public ApiResponse<Map<String, Long>> create(@PathVariable Long postId,
                                                   @Valid @RequestBody CommentCreateRequest request) {
        commentService.create(postId, request);
        return ApiResponse.success("댓글이 등록되었습니다.", Map.of("postId", postId));
    }

    @Operation(summary = "댓글 삭제(소프트 딜리트)")
    @DeleteMapping("/{commentId}")
    public ApiResponse<Void> delete(@PathVariable Long postId, @PathVariable Long commentId) {
        commentService.delete(postId, commentId);
        return ApiResponse.success("댓글이 삭제되었습니다.", null);
    }
}

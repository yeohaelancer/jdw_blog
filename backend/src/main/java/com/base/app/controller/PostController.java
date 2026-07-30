package com.base.app.controller;

import com.base.app.dto.ApiResponse;
import com.base.app.dto.PageResponse;
import com.base.app.dto.post.PostCreateRequest;
import com.base.app.dto.post.PostDetailResponse;
import com.base.app.dto.post.PostListItemResponse;
import com.base.app.dto.post.PostUpdateRequest;
import com.base.app.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Post", description = "게시글 CRUD")
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @Operation(summary = "게시글 목록 (블로그/카테고리/태그/검색 필터, 페이지네이션)")
    @GetMapping
    public ApiResponse<PageResponse<PostListItemResponse>> list(
            @RequestParam(required = false) Long blogId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int size) {
        return ApiResponse.success(postService.list(blogId, categoryId, tag, keyword, page, size));
    }

    @Operation(summary = "게시글 상세 (조회수 증가)")
    @GetMapping("/{id}")
    public ApiResponse<PostDetailResponse> detail(@PathVariable Long id) {
        return ApiResponse.success(postService.detail(id));
    }

    @Operation(summary = "게시글 작성")
    @PostMapping
    public ApiResponse<Map<String, Long>> create(@Valid @RequestBody PostCreateRequest request) {
        Long id = postService.create(request);
        return ApiResponse.success("게시글이 등록되었습니다.", Map.of("id", id));
    }

    @Operation(summary = "게시글 수정")
    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody PostUpdateRequest request) {
        postService.update(id, request);
        return ApiResponse.success("게시글이 수정되었습니다.", null);
    }

    @Operation(summary = "게시글 삭제")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        postService.delete(id);
        return ApiResponse.success("게시글이 삭제되었습니다.", null);
    }
}

package com.base.app.controller;

import com.base.app.dto.ApiResponse;
import com.base.app.mapper.TagMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Tag(name = "Tag", description = "태그 클라우드")
@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagMapper tagMapper;

    @Operation(summary = "블로그별 태그 클라우드 (게시글 수 포함)")
    @GetMapping("/cloud")
    public ApiResponse<List<Map<String, Object>>> cloud(@RequestParam Long blogId) {
        return ApiResponse.success(tagMapper.findCloudByBlogId(blogId));
    }
}

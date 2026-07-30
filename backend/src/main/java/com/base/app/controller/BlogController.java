package com.base.app.controller;

import com.base.app.dto.ApiResponse;
import com.base.app.dto.BlogDto;
import com.base.app.exception.NotFoundException;
import com.base.app.mapper.BlogMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Blog", description = "블로그 정보 조회")
@RestController
@RequestMapping("/api/blogs")
@RequiredArgsConstructor
public class BlogController {

    private final BlogMapper blogMapper;

    @Operation(summary = "블로그 상세")
    @GetMapping("/{id}")
    public ApiResponse<BlogDto> detail(@PathVariable Long id) {
        BlogDto blog = blogMapper.findById(id);
        if (blog == null) {
            throw new NotFoundException("블로그를 찾을 수 없습니다.");
        }
        return ApiResponse.success(blog);
    }
}

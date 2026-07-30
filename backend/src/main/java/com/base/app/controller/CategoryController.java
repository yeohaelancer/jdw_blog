package com.base.app.controller;

import com.base.app.dto.ApiResponse;
import com.base.app.dto.CategoryDto;
import com.base.app.mapper.CategoryMapper;
import com.base.app.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Category", description = "블로그 카테고리 조회/관리 (계층형)")
@RestController
@RequestMapping("/api/blogs/{blogId}/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryMapper categoryMapper;
    private final CategoryService categoryService;

    @Operation(summary = "카테고리 목록")
    @GetMapping
    public ApiResponse<List<CategoryDto>> list(@PathVariable Long blogId) {
        return ApiResponse.success(categoryMapper.findByBlogId(blogId));
    }

    @Operation(summary = "카테고리 생성 (블로그 소유자 또는 관리자)")
    @PostMapping
    public ApiResponse<Map<String, Long>> create(@PathVariable Long blogId, @Valid @RequestBody CategoryRequest request) {
        Long id = categoryService.create(blogId, request.toDto());
        return ApiResponse.success("카테고리가 생성되었습니다.", Map.of("id", id));
    }

    @Operation(summary = "카테고리 수정 (블로그 소유자 또는 관리자)")
    @PutMapping("/{categoryId}")
    public ApiResponse<Void> update(@PathVariable Long blogId, @PathVariable Long categoryId,
                                     @Valid @RequestBody CategoryRequest request) {
        categoryService.update(blogId, categoryId, request.toDto());
        return ApiResponse.success("카테고리가 수정되었습니다.", null);
    }

    @Operation(summary = "카테고리 삭제 (블로그 소유자 또는 관리자)")
    @DeleteMapping("/{categoryId}")
    public ApiResponse<Void> delete(@PathVariable Long blogId, @PathVariable Long categoryId) {
        categoryService.delete(blogId, categoryId);
        return ApiResponse.success("카테고리가 삭제되었습니다.", null);
    }

    public static class CategoryRequest {
        @NotBlank
        public String name;
        public Long parentId;
        public Integer sortOrder;

        CategoryDto toDto() {
            CategoryDto dto = new CategoryDto();
            dto.setName(name);
            dto.setParentId(parentId);
            dto.setSortOrder(sortOrder);
            return dto;
        }
    }
}

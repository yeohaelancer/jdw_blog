package com.base.app.service;

import com.base.app.config.AuthContext;
import com.base.app.dto.BlogDto;
import com.base.app.dto.CategoryDto;
import com.base.app.dto.UserDto;
import com.base.app.exception.ForbiddenException;
import com.base.app.exception.NotFoundException;
import com.base.app.mapper.BlogMapper;
import com.base.app.mapper.CategoryMapper;
import com.base.app.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryMapper categoryMapper;
    private final BlogMapper blogMapper;
    private final UserMapper userMapper;

    @Transactional
    public Long create(Long blogId, CategoryDto request) {
        requireBlogOwnerOrAdmin(blogId);

        CategoryDto category = new CategoryDto();
        category.setBlogId(blogId);
        category.setParentId(request.getParentId());
        category.setName(request.getName());
        category.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        categoryMapper.insert(category);
        return category.getId();
    }

    @Transactional
    public void update(Long blogId, Long categoryId, CategoryDto request) {
        requireBlogOwnerOrAdmin(blogId);
        CategoryDto existing = requireCategoryInBlog(blogId, categoryId);

        existing.setName(request.getName());
        existing.setParentId(request.getParentId());
        existing.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : existing.getSortOrder());
        categoryMapper.update(existing);
    }

    @Transactional
    public void delete(Long blogId, Long categoryId) {
        requireBlogOwnerOrAdmin(blogId);
        requireCategoryInBlog(blogId, categoryId);
        categoryMapper.softDelete(categoryId);
    }

    private CategoryDto requireCategoryInBlog(Long blogId, Long categoryId) {
        CategoryDto category = categoryMapper.findById(categoryId);
        if (category == null || !category.getBlogId().equals(blogId)) {
            throw new NotFoundException("카테고리를 찾을 수 없습니다.");
        }
        return category;
    }

    private void requireBlogOwnerOrAdmin(Long blogId) {
        Long userId = AuthContext.requireUserId();

        UserDto user = userMapper.findById(userId);
        if (user != null && "ADMIN".equals(user.getRole())) {
            return;
        }

        BlogDto blog = blogMapper.findById(blogId);
        if (blog == null) {
            throw new NotFoundException("블로그를 찾을 수 없습니다.");
        }
        if (!blog.getUserId().equals(userId)) {
            throw new ForbiddenException("카테고리 관리 권한이 없습니다.");
        }
    }
}

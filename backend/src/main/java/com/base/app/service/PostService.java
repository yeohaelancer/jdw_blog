package com.base.app.service;

import com.base.app.config.AuthContext;
import com.base.app.dto.BlogDto;
import com.base.app.dto.CategoryDto;
import com.base.app.dto.PageResponse;
import com.base.app.dto.post.PostCreateRequest;
import com.base.app.dto.post.PostDetailResponse;
import com.base.app.dto.post.PostListItemResponse;
import com.base.app.dto.post.PostUpdateRequest;
import com.base.app.exception.ForbiddenException;
import com.base.app.exception.NotFoundException;
import com.base.app.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private static final String DEFAULT_CATEGORY_NAME = "기타";

    private final PostMapper postMapper;
    private final BlogMapper blogMapper;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;
    private final PostTagMapper postTagMapper;
    private final PostLikeMapper postLikeMapper;

    public PageResponse<PostListItemResponse> list(Long blogId, Long categoryId, String tag, String keyword,
                                                     int page, int size) {
        Long viewerUserId = AuthContext.getUserId();
        boolean publicOnly = !isOwner(blogId, viewerUserId);
        int offset = (page - 1) * size;

        List<PostListItemResponse> items = postMapper.findList(blogId, categoryId, tag, keyword, publicOnly, offset, size);
        for (PostListItemResponse item : items) {
            item.setTags(tagMapper.findNamesByPostId(item.getId()));
        }
        long total = postMapper.countList(blogId, categoryId, tag, keyword, publicOnly);
        return PageResponse.of(items, page, size, total);
    }

    @Transactional
    public PostDetailResponse detail(Long id) {
        PostDetailResponse post = postMapper.findDetailById(id);
        if (post == null) {
            throw new NotFoundException("게시글을 찾을 수 없습니다.");
        }

        Long viewerUserId = AuthContext.getUserId();
        boolean owner = viewerUserId != null && viewerUserId.equals(post.getAuthorUserId());
        if (!"PUBLIC".equals(post.getVisibility()) && !owner) {
            throw new ForbiddenException("비공개 게시글입니다.");
        }

        postMapper.incrementViewCount(id);
        post.setTags(tagMapper.findNamesByPostId(id));
        if (viewerUserId != null) {
            Boolean liked = postLikeMapper.findActiveState(id, viewerUserId);
            post.setLikedByMe(Boolean.TRUE.equals(liked));
        }
        return post;
    }

    @Transactional
    public Long create(PostCreateRequest request) {
        Long userId = AuthContext.requireUserId();
        BlogDto blog = blogMapper.findByUserId(userId);
        if (blog == null) {
            throw new NotFoundException("블로그가 존재하지 않습니다.");
        }

        Long categoryId = resolveCategoryId(blog.getId(), request.getCategoryId());

        Map<String, Object> post = new HashMap<>();
        post.put("blogId", blog.getId());
        post.put("categoryId", categoryId);
        post.put("title", request.getTitle());
        post.put("content", request.getContent());
        post.put("thumbnailUrl", request.getThumbnailUrl());
        post.put("visibility", request.getVisibility());
        post.put("status", request.getStatus());
        postMapper.insert(post);
        Long postId = ((Number) post.get("id")).longValue();

        syncTags(postId, request.getTags());
        categoryMapper.adjustPostCount(categoryId, 1);
        return postId;
    }

    @Transactional
    public void update(Long id, PostUpdateRequest request) {
        Long userId = AuthContext.requireUserId();
        requireOwner(id, userId);

        PostDetailResponse existing = postMapper.findDetailById(id);
        Long categoryId = resolveCategoryId(existing.getBlogId(), request.getCategoryId());

        Map<String, Object> post = new HashMap<>();
        post.put("id", id);
        post.put("title", request.getTitle());
        post.put("content", request.getContent());
        post.put("thumbnailUrl", request.getThumbnailUrl());
        post.put("categoryId", categoryId);
        post.put("visibility", request.getVisibility());
        post.put("status", request.getStatus());
        postMapper.update(post);

        syncTags(id, request.getTags());

        Long oldCategoryId = existing.getCategoryId();
        if (!java.util.Objects.equals(oldCategoryId, categoryId)) {
            if (oldCategoryId != null) {
                categoryMapper.adjustPostCount(oldCategoryId, -1);
            }
            categoryMapper.adjustPostCount(categoryId, 1);
        }
    }

    @Transactional
    public void updateThumbnail(Long id, String thumbnailUrl) {
        Long userId = AuthContext.requireUserId();
        requireOwner(id, userId);
        postMapper.updateThumbnail(id, thumbnailUrl);
    }

    @Transactional
    public void delete(Long id) {
        Long userId = AuthContext.requireUserId();
        requireOwner(id, userId);

        PostDetailResponse existing = postMapper.findDetailById(id);
        postMapper.softDelete(id);
        if (existing != null && existing.getCategoryId() != null) {
            categoryMapper.adjustPostCount(existing.getCategoryId(), -1);
        }
    }

    /**
     * 카테고리를 지정하지 않고 작성한 글은 블로그별 "기타" 카테고리로 자동 분류한다.
     * 해당 블로그에 "기타" 카테고리가 없으면 최초 1회 생성한다.
     */
    private Long resolveCategoryId(Long blogId, Long requestedCategoryId) {
        if (requestedCategoryId != null) {
            return requestedCategoryId;
        }
        CategoryDto defaultCategory = categoryMapper.findByBlogIdAndName(blogId, DEFAULT_CATEGORY_NAME);
        if (defaultCategory != null) {
            return defaultCategory.getId();
        }
        CategoryDto created = new CategoryDto();
        created.setBlogId(blogId);
        created.setName(DEFAULT_CATEGORY_NAME);
        created.setSortOrder(9999);
        categoryMapper.insert(created);
        return created.getId();
    }

    private void syncTags(Long postId, List<String> tagNames) {
        postTagMapper.deleteByPostId(postId);
        if (tagNames == null) {
            return;
        }
        for (String raw : tagNames) {
            String name = raw == null ? "" : raw.trim();
            if (name.isEmpty()) {
                continue;
            }
            Long tagId = tagMapper.findIdByName(name);
            if (tagId == null) {
                tagMapper.insert(name);
                tagId = tagMapper.findIdByName(name);
            }
            postTagMapper.insert(postId, tagId);
        }
    }

    private boolean isOwner(Long blogId, Long userId) {
        if (blogId == null || userId == null) {
            return false;
        }
        BlogDto blog = blogMapper.findById(blogId);
        return blog != null && blog.getUserId().equals(userId);
    }

    private void requireOwner(Long postId, Long userId) {
        Long ownerUserId = postMapper.findOwnerUserId(postId);
        if (ownerUserId == null) {
            throw new NotFoundException("게시글을 찾을 수 없습니다.");
        }
        if (!ownerUserId.equals(userId)) {
            throw new ForbiddenException("본인의 게시글만 수정/삭제할 수 있습니다.");
        }
    }
}

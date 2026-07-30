package com.base.app.dto.post;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class PostDetailResponse {
    private Long id;
    private Long blogId;
    private Long authorUserId;
    private String blogName;
    private String authorNickname;
    private String authorProfileImageUrl;
    private String title;
    private String content;
    private String thumbnailUrl;
    private Long categoryId;
    private String categoryName;
    private String visibility;
    private String status;
    private Long viewCount;
    private Long likeCount;
    private Long commentCount;
    private boolean likedByMe;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private List<String> tags;
}

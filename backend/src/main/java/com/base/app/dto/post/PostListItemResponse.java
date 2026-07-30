package com.base.app.dto.post;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class PostListItemResponse {
    private Long id;
    private Long blogId;
    private String blogName;
    private String title;
    private String summary;
    private String thumbnailUrl;
    private String categoryName;
    private Long viewCount;
    private Long likeCount;
    private Long commentCount;
    private LocalDateTime publishedAt;
    private List<String> tags;
}

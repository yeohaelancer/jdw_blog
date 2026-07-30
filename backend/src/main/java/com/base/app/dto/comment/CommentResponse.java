package com.base.app.dto.comment;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentResponse {
    private Long id;
    private Long postId;
    private Long parentId;
    private Long authorUserId;
    private String authorNickname;
    private String authorProfileImageUrl;
    private String content;
    private boolean secret;
    private boolean deleted;
    private LocalDateTime createdAt;
}

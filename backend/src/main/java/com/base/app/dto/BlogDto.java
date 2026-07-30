package com.base.app.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlogDto {
    private Long id;
    private Long userId;
    private String blogName;
    private String blogUrlSlug;
    private String intro;
    private String profileImageUrl;
    private String coverImageUrl;
    private String skinTheme;
    private Long totalVisitorCount;
}

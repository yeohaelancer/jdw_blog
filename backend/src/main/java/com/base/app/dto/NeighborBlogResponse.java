package com.base.app.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NeighborBlogResponse {
    private Long blogId;
    private String blogName;
    private String profileImageUrl;
    private String neighborType;
}

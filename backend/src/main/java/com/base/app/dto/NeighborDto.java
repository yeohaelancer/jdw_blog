package com.base.app.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NeighborDto {
    private Long id;
    private Long fromBlogId;
    private Long toBlogId;
    private String neighborType;
    private boolean active;
}

package com.base.app.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryDto {
    private Long id;
    private Long blogId;
    private Long parentId;
    private String name;
    private Integer sortOrder;
    private Integer postCount;
}

package com.base.app.dto.post;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostUpdateRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    private String thumbnailUrl;
    private Long categoryId;
    private String visibility;
    private String status;
    private List<String> tags;
}

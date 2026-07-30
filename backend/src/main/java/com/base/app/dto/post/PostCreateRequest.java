package com.base.app.dto.post;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostCreateRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    private String thumbnailUrl;
    private Long categoryId;

    /** PUBLIC, NEIGHBOR, PRIVATE */
    private String visibility = "PUBLIC";

    /** DRAFT, PUBLISHED */
    private String status = "PUBLISHED";

    private List<String> tags;
}

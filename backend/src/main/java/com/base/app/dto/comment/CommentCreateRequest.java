package com.base.app.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentCreateRequest {

    @NotBlank
    @Size(max = 1000)
    private String content;

    private Long parentId;

    private boolean secret;
}

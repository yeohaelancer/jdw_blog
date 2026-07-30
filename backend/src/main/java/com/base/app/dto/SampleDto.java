package com.base.app.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SampleDto {

    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;

}

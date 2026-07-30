package com.base.app.dto.admin;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AdminPostSummary {
    private Long id;
    private Long blogId;
    private String blogName;
    private String authorNickname;
    private String title;
    private String visibility;
    private String status;
    private LocalDateTime createdAt;
}

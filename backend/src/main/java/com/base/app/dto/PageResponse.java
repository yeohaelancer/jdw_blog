package com.base.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PageResponse<T> {
    private List<T> items;
    private int page;
    private int size;
    private long totalCount;
    private boolean hasNext;

    public static <T> PageResponse<T> of(List<T> items, int page, int size, long totalCount) {
        boolean hasNext = (long) page * size < totalCount;
        return new PageResponse<>(items, page, size, totalCount, hasNext);
    }
}

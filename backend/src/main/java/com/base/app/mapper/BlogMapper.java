package com.base.app.mapper;

import com.base.app.dto.BlogDto;

public interface BlogMapper {

    BlogDto findByUserId(Long userId);

    BlogDto findById(Long id);

    int insert(BlogDto blogDto);
}

package com.base.app.mapper;

import com.base.app.dto.CategoryDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CategoryMapper {

    List<CategoryDto> findByBlogId(@Param("blogId") Long blogId);

    CategoryDto findById(@Param("id") Long id);

    CategoryDto findByBlogIdAndName(@Param("blogId") Long blogId, @Param("name") String name);

    int adjustPostCount(@Param("id") Long id, @Param("delta") int delta);

    int insert(CategoryDto categoryDto);

    int update(CategoryDto categoryDto);

    int softDelete(@Param("id") Long id);
}

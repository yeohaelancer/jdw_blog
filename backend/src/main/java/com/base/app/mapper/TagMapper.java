package com.base.app.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TagMapper {

    Long findIdByName(@Param("name") String name);

    int insert(@Param("name") String name);

    List<String> findNamesByPostId(@Param("postId") Long postId);

    List<Map<String, Object>> findCloudByBlogId(@Param("blogId") Long blogId);
}

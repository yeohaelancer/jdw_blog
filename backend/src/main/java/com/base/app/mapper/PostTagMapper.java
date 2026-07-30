package com.base.app.mapper;

import org.apache.ibatis.annotations.Param;

public interface PostTagMapper {

    int insert(@Param("postId") Long postId, @Param("tagId") Long tagId);

    int deleteByPostId(@Param("postId") Long postId);
}

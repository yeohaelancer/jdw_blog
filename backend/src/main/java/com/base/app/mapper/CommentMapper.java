package com.base.app.mapper;

import com.base.app.dto.comment.CommentResponse;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CommentMapper {

    int insert(@Param("postId") Long postId, @Param("userId") Long userId, @Param("parentId") Long parentId,
               @Param("content") String content, @Param("secret") boolean secret);

    List<CommentResponse> findByPostId(@Param("postId") Long postId);

    Long findAuthorUserId(@Param("id") Long id);

    Long findParentId(@Param("id") Long id);

    int softDelete(@Param("id") Long id);
}

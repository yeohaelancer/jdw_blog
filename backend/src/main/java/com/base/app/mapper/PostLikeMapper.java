package com.base.app.mapper;

import org.apache.ibatis.annotations.Param;

public interface PostLikeMapper {

    /** 1: 좋아요 상태(deleted_at NULL), 0: 취소 상태(deleted_at NOT NULL), null: row 없음 */
    Boolean findActiveState(@Param("postId") Long postId, @Param("userId") Long userId);

    int insert(@Param("postId") Long postId, @Param("userId") Long userId);

    int activate(@Param("postId") Long postId, @Param("userId") Long userId);

    int deactivate(@Param("postId") Long postId, @Param("userId") Long userId);
}

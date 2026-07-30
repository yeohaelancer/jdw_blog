package com.base.app.mapper;

import com.base.app.dto.admin.AdminPostSummary;
import com.base.app.dto.post.PostDetailResponse;
import com.base.app.dto.post.PostListItemResponse;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface PostMapper {

    /** 관리자 전용: 전체 블로그의 게시글을 공개범위/상태 무관하게 최신순 조회 */
    List<AdminPostSummary> findAllForAdmin();

    int insert(Map<String, Object> post);

    PostDetailResponse findDetailById(@Param("id") Long id);

    List<PostListItemResponse> findList(@Param("blogId") Long blogId,
                                         @Param("categoryId") Long categoryId,
                                         @Param("tag") String tag,
                                         @Param("keyword") String keyword,
                                         @Param("publicOnly") boolean publicOnly,
                                         @Param("offset") int offset,
                                         @Param("size") int size);

    long countList(@Param("blogId") Long blogId,
                    @Param("categoryId") Long categoryId,
                    @Param("tag") String tag,
                    @Param("keyword") String keyword,
                    @Param("publicOnly") boolean publicOnly);

    int update(Map<String, Object> post);

    int softDelete(@Param("id") Long id);

    int incrementViewCount(@Param("id") Long id);

    int adjustLikeCount(@Param("id") Long id, @Param("delta") int delta);

    int adjustCommentCount(@Param("id") Long id, @Param("delta") int delta);

    /** blogId, ownerUserId 조회용 - 소유자 검증 */
    Long findOwnerUserId(@Param("id") Long id);
}

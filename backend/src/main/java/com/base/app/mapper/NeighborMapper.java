package com.base.app.mapper;

import com.base.app.dto.NeighborBlogResponse;
import com.base.app.dto.NeighborDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface NeighborMapper {

    /** soft-delete 여부와 무관하게 from->to 관계 row 를 찾는다 (재추가 시 재사용) */
    NeighborDto findRelation(@Param("fromBlogId") Long fromBlogId, @Param("toBlogId") Long toBlogId);

    int insert(@Param("fromBlogId") Long fromBlogId, @Param("toBlogId") Long toBlogId, @Param("type") String type);

    int activate(@Param("id") Long id, @Param("type") String type);

    int updateType(@Param("id") Long id, @Param("type") String type);

    int deactivate(@Param("id") Long id);

    List<NeighborBlogResponse> findMyNeighbors(@Param("fromBlogId") Long fromBlogId);
}

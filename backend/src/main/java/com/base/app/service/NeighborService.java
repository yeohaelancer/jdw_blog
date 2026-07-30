package com.base.app.service;

import com.base.app.config.AuthContext;
import com.base.app.dto.BlogDto;
import com.base.app.dto.NeighborBlogResponse;
import com.base.app.dto.NeighborDto;
import com.base.app.exception.NotFoundException;
import com.base.app.mapper.BlogMapper;
import com.base.app.mapper.NeighborMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NeighborService {

    private static final String ONE_SIDED = "ONE_SIDED";
    private static final String MUTUAL = "MUTUAL";

    private final NeighborMapper neighborMapper;
    private final BlogMapper blogMapper;

    public String status(Long targetBlogId) {
        Long myBlogId = myBlogId();
        NeighborDto forward = neighborMapper.findRelation(myBlogId, targetBlogId);
        return (forward != null && forward.isActive()) ? forward.getNeighborType() : null;
    }

    @Transactional
    public void add(Long targetBlogId) {
        Long myBlogId = myBlogId();
        if (myBlogId.equals(targetBlogId)) {
            throw new IllegalArgumentException("자기 자신은 이웃으로 추가할 수 없습니다.");
        }
        if (blogMapper.findById(targetBlogId) == null) {
            throw new NotFoundException("대상 블로그를 찾을 수 없습니다.");
        }

        NeighborDto forward = neighborMapper.findRelation(myBlogId, targetBlogId);
        if (forward != null && forward.isActive()) {
            throw new IllegalArgumentException("이미 이웃으로 추가한 블로그입니다.");
        }

        NeighborDto reverse = neighborMapper.findRelation(targetBlogId, myBlogId);
        boolean reverseActive = reverse != null && reverse.isActive();
        String newType = reverseActive ? MUTUAL : ONE_SIDED;

        if (forward == null) {
            neighborMapper.insert(myBlogId, targetBlogId, newType);
        } else {
            neighborMapper.activate(forward.getId(), newType);
        }

        if (reverseActive && !MUTUAL.equals(reverse.getNeighborType())) {
            neighborMapper.updateType(reverse.getId(), MUTUAL);
        }
    }

    @Transactional
    public void remove(Long targetBlogId) {
        Long myBlogId = myBlogId();
        NeighborDto forward = neighborMapper.findRelation(myBlogId, targetBlogId);
        if (forward == null || !forward.isActive()) {
            throw new IllegalArgumentException("이웃 관계가 아닙니다.");
        }
        neighborMapper.deactivate(forward.getId());

        NeighborDto reverse = neighborMapper.findRelation(targetBlogId, myBlogId);
        if (reverse != null && reverse.isActive() && MUTUAL.equals(reverse.getNeighborType())) {
            neighborMapper.updateType(reverse.getId(), ONE_SIDED);
        }
    }

    public List<NeighborBlogResponse> myList() {
        return neighborMapper.findMyNeighbors(myBlogId());
    }

    private Long myBlogId() {
        Long userId = AuthContext.requireUserId();
        BlogDto blog = blogMapper.findByUserId(userId);
        if (blog == null) {
            throw new NotFoundException("블로그가 존재하지 않습니다.");
        }
        return blog.getId();
    }
}

package com.base.app.service;

import com.base.app.config.AuthContext;
import com.base.app.mapper.PostLikeMapper;
import com.base.app.mapper.PostMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {

    private final PostLikeMapper postLikeMapper;
    private final PostMapper postMapper;

    /**
     * @return 토글 후 좋아요 상태(true=좋아요됨)
     */
    public boolean toggle(Long postId) {
        Long userId = AuthContext.requireUserId();
        Boolean currentlyActive = postLikeMapper.findActiveState(postId, userId);

        if (currentlyActive == null) {
            postLikeMapper.insert(postId, userId);
            postMapper.adjustLikeCount(postId, 1);
            return true;
        }
        if (currentlyActive) {
            postLikeMapper.deactivate(postId, userId);
            postMapper.adjustLikeCount(postId, -1);
            return false;
        }
        postLikeMapper.activate(postId, userId);
        postMapper.adjustLikeCount(postId, 1);
        return true;
    }
}

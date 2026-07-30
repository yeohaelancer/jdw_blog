package com.base.app.service;

import com.base.app.config.AuthContext;
import com.base.app.dto.comment.CommentCreateRequest;
import com.base.app.dto.comment.CommentResponse;
import com.base.app.exception.ForbiddenException;
import com.base.app.exception.NotFoundException;
import com.base.app.mapper.CommentMapper;
import com.base.app.mapper.PostMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentMapper commentMapper;
    private final PostMapper postMapper;

    public List<CommentResponse> list(Long postId) {
        return commentMapper.findByPostId(postId);
    }

    @Transactional
    public Long create(Long postId, CommentCreateRequest request) {
        Long userId = AuthContext.requireUserId();

        if (request.getParentId() != null) {
            // Review 지적사항: DB는 무한 depth를 허용하나 Designer 명세는 대댓글 1단계까지만 지원
            Long parentOfParent = commentMapper.findParentId(request.getParentId());
            if (parentOfParent != null) {
                throw new IllegalArgumentException("대댓글에는 답글을 작성할 수 없습니다.");
            }
        }

        commentMapper.insert(postId, userId, request.getParentId(), request.getContent(), request.isSecret());
        postMapper.adjustCommentCount(postId, 1);
        return userId;
    }

    @Transactional
    public void delete(Long postId, Long commentId) {
        Long userId = AuthContext.requireUserId();
        Long authorUserId = commentMapper.findAuthorUserId(commentId);
        if (authorUserId == null) {
            throw new NotFoundException("댓글을 찾을 수 없습니다.");
        }
        if (!authorUserId.equals(userId)) {
            throw new ForbiddenException("본인의 댓글만 삭제할 수 있습니다.");
        }
        commentMapper.softDelete(commentId);
        postMapper.adjustCommentCount(postId, -1);
    }
}

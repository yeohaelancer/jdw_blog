# jdw_blog ERD (blog_ prefix)

## 1. 엔티티 분석
- 주요 엔티티: 회원(blog_user), 블로그(blog_blog), 이웃(blog_neighbor), 카테고리(blog_category),
  게시글(blog_post), 게시글이미지(blog_post_image), 태그(blog_tag), 게시글-태그(blog_post_tag),
  댓글(blog_comment), 공감(blog_post_like), 방문자로그(blog_visitor_log), 신고(blog_report),
  리프레시토큰(blog_refresh_token)
- 관계 유형:
  - 회원 1 : 1 블로그 (회원가입 시 자동 개설)
  - 회원 1 : N 리프레시토큰
  - 블로그 1 : N 카테고리 (카테고리는 self-reference로 계층형 구조)
  - 블로그 1 : N 게시글
  - 블로그 N : M 블로그 (이웃 관계, blog_neighbor로 단방향 2건이 모이면 MUTUAL)
  - 카테고리 1 : N 게시글
  - 게시글 1 : N 게시글이미지
  - 게시글 N : M 태그 (blog_post_tag 중간테이블)
  - 게시글 1 : N 댓글 (댓글은 self-reference로 대댓글 1단계)
  - 게시글 N : M 회원 (좋아요, blog_post_like 중간테이블)
  - 블로그 1 : N 방문자로그
  - 회원 1 : N 신고 (신고자/처리자 모두 회원 참조)

## 2. ERD 설명 (텍스트)

```
blog_user 1 --- 1 blog_blog
blog_user 1 --- N blog_refresh_token
blog_user 1 --- N blog_comment
blog_user 1 --- N blog_post_like
blog_user 1 --- N blog_report (reporter)
blog_user 1 --- N blog_report (handler)

blog_blog 1 --- N blog_category
blog_category 1 --- N blog_category (self, parent_id)
blog_blog 1 --- N blog_post
blog_blog N --- N blog_blog (blog_neighbor: from_blog_id / to_blog_id)
blog_blog 1 --- N blog_visitor_log

blog_category 1 --- N blog_post

blog_post 1 --- N blog_post_image
blog_post 1 --- N blog_comment
blog_comment 1 --- N blog_comment (self, parent_id)
blog_post N --- N blog_tag (blog_post_tag)
blog_post N --- N blog_user (blog_post_like)
```

## 3. 테이블 목록

| 테이블 | 설명 |
|---|---|
| blog_user | 회원 |
| blog_refresh_token | JWT Refresh Token 저장 |
| blog_blog | 블로그(회원 1:1) |
| blog_neighbor | 이웃/팔로우 관계 |
| blog_category | 게시글 카테고리 (계층형) |
| blog_post | 게시글 |
| blog_post_image | 게시글 첨부 이미지 |
| blog_tag | 태그 |
| blog_post_tag | 게시글-태그 매핑 |
| blog_comment | 댓글/대댓글 |
| blog_post_like | 게시글 공감(좋아요) |
| blog_visitor_log | 블로그 방문자 로그 |
| blog_report | 신고/제재 (관리자 확장) |

DDL 원본: [schema.sql](schema.sql)

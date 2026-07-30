## 🧐 Review Agent 검토 결과

### 검토 대상
- DBA 산출물: `docs/agents/dba/` (schema.sql, ERD.md)
- Designer 산출물: `docs/agents/designer/` (design-system.md, components.md, screens/)

### 발견 이슈

| 심각도 | 항목 | 설명 | 권고 조치 |
|--------|------|------|----------|
| 🟡 중간 | PostCard 요약(summary) | `blog-home.md`의 `PostCard`는 `summary` 필드를 요구하나 `blog_post`에는 `content`(LONGTEXT)만 존재, 별도 요약 컬럼 없음 | DB 컬럼 추가 대신 **Dev 단에서 content HTML을 파싱해 앞부분 텍스트 100~150자를 잘라 응답 DTO에 담아 반환**(서버 계산, 클라이언트 파싱 금지 — XSS 위험) |
| 🟡 중간 | 공감(좋아요) soft-delete 토글 재사용 | `blog_post_like`는 `UNIQUE(post_id, user_id)` + soft delete 구조. 좋아요 취소 후 재클릭 시 새 row INSERT하면 UNIQUE 제약 위반 | Dev는 좋아요 토글 시 **기존 row 존재 여부 확인 후 UPDATE(deleted_at NULL/NOW())**로 처리, INSERT는 최초 1회만 |
| 🟡 중간 | 댓글 depth 제한 | `blog_comment.parent_id`는 self-FK로 이론상 무한 depth 가능하나, Designer는 대댓글 1단계(depth=1)까지만 명세 | DB 레벨 제약 없음 — **Dev 애플리케이션 레이어에서 parent 댓글이 이미 대댓글(parent_id NOT NULL)이면 추가 대댓글 생성 차단** |
| 🟢 낮음 | 조회수 증가 어뷰징 | `blog_post.view_count` 단순 증가 컬럼, 동일 사용자 새로고침 반복 시 무한 증가 | Dev에서 세션/쿠키 기준 중복 조회 방지(예: 1일 1회) 권고 — DB 변경 불필요 |
| 🟢 낮음 | CategoryTree post_count 정합성 | `blog_category.post_count`는 비정규화 캐시 컬럼, 게시글 CRUD/카테고리 이동 시 동기화 누락 위험 | Dev는 게시글 생성/삭제/카테고리 변경 트랜잭션 내에서 반드시 함께 갱신 |
| 🟢 낮음 | 태그 클라우드 빈도 | Designer는 "빈도 비례 크기" TagChip을 요구하나 집계 API 별도 명세 없음 | Dev가 `GET /tags/cloud?blogId=` 형태로 태그별 게시글 수 집계 API 추가 필요 |

### 개선 권고사항
1. 위 🟡 항목 3건은 Dev 구현 시 **필수 반영**해야 함(데이터 정합성/보안 직결).
2. 이번 이터레이션 범위(인증/게시글/댓글/공감)에서는 `blog_neighbor`, `blog_report` 스키마는 사용하지 않음 — 향후 이웃/관리자 기능 구현 시 재검토.
3. 다크모드 대응은 Designer가 토큰 레벨에서 정의했으므로 Dev는 CSS 변수만 사용하고 하드코딩 색상 금지.

### 📤 Dev Agent 전달 주의사항
- **반드시 반영할 사항**: 좋아요 UPDATE 방식 토글, 댓글 depth 1단계 제한(서버 검증), 게시글 요약은 서버에서 생성
- **선택적 개선사항**: 조회수 중복 방지, 카테고리 post_count 동기화, 태그 클라우드 API

### ✅ 검토 판정
- [x] 조건부 통과 (경미한 이슈 Dev 진행 중 수정) — 🔴 높음 이슈 없음, 🟡 3건은 Dev 구현 시 즉시 반영 조건

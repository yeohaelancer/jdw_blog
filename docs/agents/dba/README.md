## 🗄️ DBA Agent 산출물

### 시드 데이터 (2차 요청 반영)
- `blog_user` id=8 (`hjgy555@gmail.com`, 닉네임 "여해")를 `role='ADMIN'`으로 승격
- `blog_category`에 해당 사용자의 블로그(blog_id=8) 기준으로 `낚시/골프/여행/캠핑/레시피` 5건 삽입 (sort_order 1~5)
- DDL(`schema.sql`)에는 포함하지 않고 운영 DB에 직접 반영 — 신규 환경 셋업 시에는 별도로 관리자 승격 + 초기 카테고리 삽입 필요

### 1. 엔티티 분석
- 주요 엔티티: 회원, 블로그, 이웃, 카테고리, 게시글, 태그, 댓글, 공감, 방문자로그, 신고
- 관계 유형: 상세는 [ERD.md](ERD.md) 참조

### 2. ERD 설명
[ERD.md](ERD.md) 참조

### 3. DDL 스크립트
[schema.sql](schema.sql) — MySQL 8.0 대상, `jdworks` DB에 **직접 실행 완료**
(연결: `127.0.0.1:3306`, user `jdwuser`, 총 13개 테이블 생성 확인)

### 4. 인덱스 전략
- `blog_user`: email(UNIQUE), (social_provider, social_id) UNIQUE — 소셜 로그인 중복 방지
- `blog_blog`: user_id(UNIQUE, 1:1 보장), blog_url_slug(UNIQUE) — 블로그 주소 라우팅용
- `blog_post`: (status, visibility, published_at) 복합 인덱스 — 공개글 피드 조회 최적화
- `blog_post`: FULLTEXT(title, content) — 게시글 내 검색 기능 지원
- `blog_comment`: post_id, parent_id 인덱스 — 게시글별 댓글 트리 조회
- `blog_post_like`: (post_id, user_id) UNIQUE — 중복 좋아요 방지 + 토글 조회
- `blog_neighbor`: (from_blog_id, to_blog_id) UNIQUE — 중복 이웃신청 방지
- `blog_visitor_log`: (blog_id, visit_date) — 일별 방문자 집계 조회 최적화

### 5. 최적화 권고
- **파티셔닝**: `blog_visitor_log`는 방문 로그가 빠르게 누적되므로 `visit_date` 기준 월별 RANGE 파티셔닝 고려. 일정 기간(예: 1년) 경과 데이터는 별도 집계 테이블로 아카이빙 후 원본 삭제 권고.
- **캐싱 레이어**: `blog_post.view_count`, `blog_blog.visitor_count_today` 등 카운터성 컬럼은 Redis에서 증분 처리 후 배치로 DB 반영(조회수 폭주 시 write 부하 완화).
- **검색**: 현재는 MySQL FULLTEXT 인덱스로 기본 검색을 지원하나, 통합검색/랭킹이 고도화되면 Elasticsearch/OpenSearch 연동 검토.
- 좋아요/댓글은 소프트 딜리트(`deleted_at`)로 이력을 보존하되, `blog_post.like_count`/`comment_count`는 애플리케이션 레벨에서 트랜잭션과 함께 증감 관리 필요 (트리거 대신 서비스 레이어 권장).

### 📤 Designer/Dev에게 전달할 사항
- **주요 테이블 구조 요약**
  - 회원가입 시 `blog_user` insert 트랜잭션 안에서 `blog_blog`(1:1)를 함께 생성해야 함 (blog_url_slug는 닉네임 기반 자동 생성 + 중복 시 suffix 처리 필요).
  - 게시글 공개범위는 `blog_post.visibility` (`PUBLIC`/`NEIGHBOR`/`PRIVATE`)로 제어, `blog_neighbor`에서 `MUTUAL` 여부를 조회해 NEIGHBOR 공개글 노출 여부 판단.
  - 카테고리는 `blog_category.parent_id` self-reference로 다단계 트리 구성 가능 (Designer는 2단계까지의 트리 UI만 우선 고려 권장).
  - 댓글은 `parent_id`로 1단계 대댓글만 지원(요구사항상 1~2단계). 2단계 이상 depth가 필요하면 Dev 단에서 UI로 depth 제한.
  - 비밀번호는 반드시 BCrypt 해시로 저장, `blog_user.password` 컬럼에 평문 저장 금지.

- **FK 제약 조건 주의사항**
  - 대부분 테이블이 soft delete(`deleted_at`)를 사용하므로, FK 무결성은 유지되지만 **삭제된 부모 레코드를 조회 시 애플리케이션에서 `deleted_at IS NULL` 조건을 항상 포함**해야 함 (DB 레벨에서 자동 필터링되지 않음).
  - `blog_comment.parent_id`, `blog_category.parent_id`는 self-FK이므로 최상위 레코드 삭제 시 하위 레코드 처리 정책(연쇄 소프트 딜리트 or 유지) 결정 필요 — 현재 CASCADE 미설정, 서비스 레이어에서 처리.
  - `blog_neighbor`는 단방향 레코드 2건(A→B, B→A)이 모두 존재해야 `MUTUAL`(서로이웃)로 판단하는 구조 — Dev 단에서 이 판정 로직 구현 필요.

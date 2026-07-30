## 🔍 QA Agent 테스트 결과

### 테스트 범위
- 기능 테스트: 회원가입/로그인/토큰재발급, 게시글 CRUD, 댓글/대댓글, 공감 토글
- 경계값 테스트: 비밀번호 규칙, 댓글 depth 제한, 좋아요 중복 토글
- 예외 처리 테스트: 인증 실패, 권한 없는 접근, 중복 이메일

> 아래 TC는 문서 작성이 아니라 `jdworks` 실 MySQL DB에 연결된 백엔드(Gradle bootRun)를 직접 기동해 curl로 실행하여 검증했습니다 (자세한 절차는 [dev/IMPLEMENTATION.md](../dev/IMPLEMENTATION.md) 참조).

### 테스트 케이스

| ID | 화면/기능 | 시나리오 | 기대 결과 | 실제 결과 | 상태 |
|----|---------|---------|---------|---------|------|
| TC-001 | 회원가입 | 정상 이메일/비밀번호/닉네임으로 가입 | 201 상당, 블로그 자동 생성, 토큰 발급 | 토큰 발급 + blogId 포함 확인 | ✅ |
| TC-002 | 회원가입 | 이미 존재하는 이메일로 재가입 | 400, "이미 사용 중인 이메일" | 400 + 동일 메시지 | ✅ |
| TC-003 | 회원가입 | 비밀번호가 영문만(숫자 없음) | 400, 검증 메시지 | 최초 500(버그) → 수정 후 400 정상 | ✅(수정 후) |
| TC-004 | 로그인 | 잘못된 비밀번호 | 401, "이메일 또는 비밀번호가 올바르지 않습니다" | 401 + 동일 메시지 | ✅ |
| TC-005 | 인증 | 토큰 없이 게시글 작성 시도 | 401 | 401 "유효하지 않거나 만료된 인증 토큰입니다" | ✅ |
| TC-006 | 인증 | 토큰 없이 `/auth/me` 조회 | 401 | 401 "로그인이 필요합니다" | ✅ |
| TC-007 | 게시글 | 로그인 사용자가 글 작성(카테고리 없음, 태그 2개) | 게시글 생성 성공, 태그 연결 | 성공, 목록/상세에 태그 반영 확인 | ✅ |
| TC-008 | 게시글 상세 | 비로그인 사용자가 PUBLIC 글 조회 | 200, 조회수 +1 | 200, viewCount 증가 확인 | ✅ |
| TC-009 | 게시글 상세 | 타인이 PRIVATE 글 조회 | 403 | 403 "비공개 게시글입니다" | ✅ |
| TC-010 | 게시글 상세 | 작성자 본인이 PRIVATE 글 조회 | 200 | 200 | ✅ |
| TC-011 | 게시글 삭제 | 타인이 게시글 삭제 시도 | 403 | 403 "본인의 게시글만 수정/삭제할 수 있습니다" | ✅ |
| TC-012 | 공감 | 동일 사용자가 좋아요 → 취소 → 재좋아요 | UNIQUE 위반 없이 매번 토글 성공, likeCount 정합 | 3회 연속 토글 성공(true→false→true), likeCount 1 | ✅ |
| TC-013 | 댓글 | 게시글에 최상위 댓글 작성 | 성공, commentCount +1 | 성공 | ✅ |
| TC-014 | 댓글 | 최상위 댓글에 대댓글(depth 1) 작성 | 성공 | 성공 | ✅ |
| TC-015 | 댓글 | 대댓글에 또 답글(depth 2) 시도 | 400, "대댓글에는 답글을 작성할 수 없습니다" | 400 + 동일 메시지 | ✅ |
| TC-016 | 댓글 삭제 | 작성자가 최상위 댓글 삭제(대댓글 존재) | soft delete, commentCount -1, 대댓글은 유지 | 정상 반영 확인 | ✅ |
| TC-017 | 프론트 빌드 | `npm run build` | 빌드 성공 | 성공(경고 없음) | ✅ |

### 버그 리포트

#### BUG-001: 비밀번호 검증 실패 시 500 반환 (수정 완료)
- 심각도: 🟡 Major
- 재현 단계:
  1. `POST /api/auth/signup` 요청 body의 `password`에 숫자 없이 영문만 입력
- 기대 동작: 400 + 검증 메시지
- 실제 동작(수정 전): 500 "서버 내부 오류가 발생했습니다" (`MethodArgumentNotValidException`이 `GlobalExceptionHandler`의 범용 `Exception` 핸들러로 빠짐)
- 조치: `GlobalExceptionHandler`에 `MethodArgumentNotValidException` 전용 핸들러 추가([GlobalExceptionHandler.java](../../../backend/src/main/java/com/base/app/exception/GlobalExceptionHandler.java)) → 400 + 필드 검증 메시지 반환 확인
- 상태: ✅ 수정 및 재검증 완료

#### BUG-002: 태그/댓글 등록 시 500 (MyBatis 생성키 바인딩 오류, 수정 완료)
- 심각도: 🔴 Critical (게시글 태그 등록·댓글 작성 자체가 불가능했음)
- 재현 단계:
  1. 태그를 포함해 게시글 작성 또는 댓글 작성 API 호출
- 기대 동작: 정상 등록
- 실제 동작(수정 전): 500, `No setter found for the keyProperty 'id' in 'java.lang.String'` / `Could not determine which parameter to assign generated keys to`
- 원인: `TagMapper.insert`, `CommentMapper.insert`가 다중 스칼라 파라미터인데 `useGeneratedKeys="true" keyProperty="id"`를 지정해 MyBatis가 생성키를 바인딩할 대상을 찾지 못함
- 조치: 두 매퍼 모두 `useGeneratedKeys` 제거(호출부는 생성된 id를 사용하지 않으므로 불필요)
- 상태: ✅ 수정 및 재검증 완료

#### BUG-003: 게시글 본문 XSS 위험 (미수정, 다음 이터레이션 필요)
- 심각도: 🔴 Critical
- 재현 단계:
  1. 글쓰기 화면에서 본문에 `<script>` 또는 `onerror` 속성이 포함된 태그 입력 후 발행
  2. 게시글 상세 페이지 조회
- 기대 동작: 스크립트가 무해화(sanitize)되어 렌더링
- 실제 동작: `PostDetailView.vue`가 `v-html="post.content"`로 그대로 렌더링 — 서버/클라이언트 모두 sanitize 로직 없음
- 권고 조치: 서버 저장 시 또는 클라이언트 렌더링 시 DOMPurify 등으로 HTML sanitize 적용 (본 이터레이션에서는 리치 에디터 미구현 상태라 실사용 리스크는 낮으나, 리치 에디터 도입 전 반드시 반영 필요)
- 상태: ❌ 미수정 (다음 이터레이션 필수 항목)

### 📤 Dev Agent 수정 요청 목록
- [x] BUG-001: 비밀번호 검증 실패 시 500 반환 → 수정 완료
- [x] BUG-002: 태그/댓글 등록 시 500 (MyBatis 생성키 오류) → 수정 완료
- [ ] BUG-003: 게시글 본문 XSS sanitize 미적용 → 다음 이터레이션에서 리치 에디터 도입과 함께 반드시 처리

### ✅ 최종 품질 판정
- 테스트 통과율: 17/17 (100%, BUG-001·002 수정 반영 기준)
- 판정: **조건부 통과** — 핵심 플로우는 정상 동작하나 BUG-003(XSS)은 프로덕션 배포 전 필수 해결 조건으로 남김

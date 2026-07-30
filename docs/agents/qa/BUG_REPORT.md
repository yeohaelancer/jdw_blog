# 버그 리포트 (jdw_blog 핵심 기능 QA)

전체 테스트 케이스는 [TEST_CASES.md](TEST_CASES.md) 참조. 아래는 발견된 버그 상세.

## BUG-001: 비밀번호 검증 실패 시 500 반환
- 심각도: 🟡 Major
- 재현: `POST /api/auth/signup`, `password`에 숫자 없이 영문만 입력
- 기대: 400 + 검증 메시지
- 실제(수정 전): 500 "서버 내부 오류가 발생했습니다"
- 원인: `@Valid` 검증 실패 시 발생하는 `MethodArgumentNotValidException`을 처리하는 전용 핸들러가 없어 범용 `Exception` 핸들러(500)로 처리됨
- 조치: `GlobalExceptionHandler`에 `MethodArgumentNotValidException` 핸들러 추가 → 400 반환
- 상태: ✅ 수정 완료 (재검증: 400 + "비밀번호는 8자 이상, 영문+숫자를 포함해야 합니다." 확인)

## BUG-002: 태그/댓글 등록 시 500 (MyBatis 생성키 바인딩 오류)
- 심각도: 🔴 Critical
- 재현: 태그 포함 게시글 작성, 또는 댓글/대댓글 작성 API 호출
- 기대: 정상 등록
- 실제(수정 전): 500
  - 태그: `No setter found for the keyProperty 'id' in 'java.lang.String'`
  - 댓글: `Could not determine which parameter to assign generated keys to`
- 원인: `TagMapper.insert(String name)`, `CommentMapper.insert(postId, userId, parentId, content, secret)`가 다중/단일 스칼라 파라미터인데 XML에 `useGeneratedKeys="true" keyProperty="id"`를 지정 — MyBatis가 생성된 키를 되돌려 쓸 대상(단일 객체/Map)이 없어 실패
- 조치: 두 매퍼 XML에서 `useGeneratedKeys`/`keyProperty` 제거 (서비스 로직이 생성된 id를 사용하지 않으므로 불필요)
- 상태: ✅ 수정 완료 (재검증: 댓글/대댓글 작성, 태그 포함 게시글 작성 모두 정상)

## BUG-003: 게시글 본문 XSS 위험
- 심각도: 🔴 Critical
- 재현: 게시글 본문에 스크립트/이벤트 핸들러 속성을 포함해 발행 후 상세 페이지 조회
- 기대: sanitize 되어 무해화
- 실제: `PostDetailView.vue`가 `v-html="post.content"`로 그대로 렌더링, 서버도 별도 sanitize 없음
- 조치(권고): DOMPurify 등으로 서버 저장 시 또는 클라이언트 렌더링 시 sanitize 적용
- 상태: ❌ 미수정 — **다음 이터레이션(리치 에디터 도입) 전 필수 처리 항목**

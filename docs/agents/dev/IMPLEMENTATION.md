## 💻 Dev Agent 구현 결과

### 구현 계획
- 아키텍처: SPA(Vue 3) + REST API(Spring Boot), JWT(Access/Refresh) 기반 인증, MyBatis로 `blog_` 스키마 직접 매핑
- 기술 스택: Spring Boot 3.3 + MyBatis 3 + MySQL 8 (backend) / Vue 3 + Pinia + Vue Router + Axios + Vite (frontend), 기존 스캐폴딩 그대로 사용
- 작업 순서: 인증(JWT/BCrypt) → 게시글 CRUD(카테고리/태그) → 댓글/대댓글 → 공감(좋아요) → Designer 디자인 토큰 반영 프론트엔드

### 범위
Manager 지시에 따라 이번 이터레이션은 **핵심 기능(인증, 게시글, 댓글, 공감)** 만 구현. 이웃, 소셜 로그인, 예약 발행은 DB 스키마만 준비된 상태로 다음 이터레이션 범위.

### 추가 구현: 카테고리 관리자 기능 (2차 요청)
- `blog_user.role='ADMIN'`인 사용자를 위한 사이트 관리 기능 추가
  - **카테고리 CRUD**: `CategoryController`/`CategoryService` — 블로그 소유자 **또는** ADMIN이 특정 블로그의 카테고리를 생성/수정/삭제 가능(`requireBlogOwnerOrAdmin`)
  - **회원 관리**: `AdminController`/`AdminService` — 전체 회원 목록 조회, 상태 변경(ACTIVE/SUSPENDED/WITHDRAWN)
  - **게시글 관리**: 전체 블로그의 게시글을 공개범위/상태 무관하게 조회, 소유자 무관 강제 삭제
  - 프론트엔드: `/admin/categories`, `/admin/users`, `/admin/posts` (라우터 가드로 `authStore.isAdmin`이 아니면 `/login`으로 리다이렉트), 헤더에 "관리자" 링크는 `isAdmin`일 때만 노출
- **DB 반영**: 사용자 `hjgy555@gmail.com`(닉네임 "여해", user id 8, blog id 8)을 `ADMIN`으로 승격, 해당 블로그에 `낚시/골프/여행/캠핑/레시피` 5개 카테고리 시드 삽입 완료
- **검증**: 임시 테스트 관리자 계정을 만들어 curl로 카테고리 CRUD/권한 경계(비소유자 403)/회원 상태 변경/게시글 강제삭제를 모두 확인했고, 실제 프론트엔드 로그인 후 브라우저에서 관리자 화면 3종(카테고리 추가/삭제, 회원 목록, 게시글 목록) 동작까지 확인. 검증에 사용한 임시 관리자 계정은 이후 다시 `USER`로 되돌려 정리함

### 백엔드 구현
- **인증**: `AuthController`/`AuthService` — 회원가입 시 `blog_user` + `blog_blog` 트랜잭션 동시 생성(1:1), BCrypt 해시(`spring-security-crypto` 단독 사용), JWT Access(30분)/Refresh(7일) 발급(`JwtTokenProvider`), `AuthInterceptor`가 GET은 optional-auth, 그 외 메서드는 필수 인증으로 처리
- **게시글**: `PostController`/`PostService` — 목록(페이지네이션, 카테고리/태그/키워드 필터, FULLTEXT 검색), 상세(조회수 증가 + 로그인 시 좋아요 여부 포함), 생성/수정/삭제(소유자 검증), 태그 동기화, 카테고리 post_count 동기화
- **댓글**: `CommentController`/`CommentService` — 대댓글 1단계 제한을 애플리케이션 레이어에서 검증(Review 지적사항 반영), 소프트 딜리트
- **공감**: `LikeController`/`LikeService` — soft-delete 재사용 UPDATE 기반 토글(Review 지적사항 반영, UNIQUE 제약 위반 방지)
- **부가**: `CategoryController`(카테고리 트리), `TagController`(태그 클라우드), `BlogController`(블로그 정보)
- 공통: `GlobalExceptionHandler`에 401/403/404 매핑 추가, `WebConfig`에 JWT 인터셉터 등록

### 프론트엔드 구현
- Designer 토큰(`design-system.md`)을 `src/assets/main.css`의 CSS 변수로 이식(라이트/다크 모두 대응)
- 컴포넌트: `AppButton`, `WatercolorCard`, `TagChip`, `LikeHeart`(낙관적 업데이트+파티클), `PostCard`, `SpeechBubbleComment`(말풍선 꼬리 CSS, 대댓글 들여쓰기)
- 화면: `LoginView`, `SignupView`(이메일 중복확인 인라인), `BlogHomeView`(카테고리/태그 필터 + 무한 아님, 더보기 페이지네이션), `PostDetailView`(좋아요/댓글/답글), `PostEditorView`(30초 자동저장 + 로컬스토리지 fallback)
- `store/auth.js`(Pinia): 세션 상태 + accessToken/refreshToken localStorage 관리
- `api/http.js`: Authorization 헤더 자동 첨부, 401 발생 시 refresh 토큰으로 1회 자동 재시도

### 검증
- `npm run build` 정상 완료 (Vite 프로덕션 빌드 통과)
- 백엔드는 Gradle wrapper jar 부재로 `gradlew` 직접 사용 불가 → Gradle 8.10을 임시로 받아 JDK 21 툴체인으로 **실제 컴파일 + `bootRun`으로 기동, `jdworks` MySQL에 대해 curl로 전체 API 흐름 실측 검증** 후 툴체인은 원래 설정(Java 17)으로 원복
  - 검증한 흐름: 회원가입(블로그 자동생성) → 로그인 → 게시글 작성/목록/상세(조회수 증가) → 좋아요 토글(생성→취소→재생성, UNIQUE 위반 없음 확인) → 댓글 작성 → 대댓글 작성(성공) → 대댓글의 대댓글(400 차단 확인) → 댓글 삭제(commentCount 동기화 확인)
  - 검증 중 실제 버그 2건 발견 및 수정: `TagMapper.insert`/`CommentMapper.insert`가 다중 스칼라 파라미터에 `useGeneratedKeys`를 잘못 지정해 MyBatis가 생성키를 바인딩할 대상을 찾지 못해 500 에러 발생 → 두 매퍼 모두 `useGeneratedKeys` 제거(생성된 id를 호출부에서 쓰지 않으므로 불필요)

### 실행 방법
```bash
# backend
cd backend
# JDK 17 + backend/.env(DB 접속정보, jdworks 로 이미 설정됨) 필요
./gradlew bootRun   # gradle-wrapper.jar 미포함 시 `gradle wrapper` 로 먼저 생성

# frontend
cd frontend
npm install
npm run dev
```

### 📤 QA Agent 전달 사항
- **테스트 가능한 엔드포인트/화면**: `/signup`, `/login`, `/`(블로그 홈), `/write`, `/posts/:id`
- **알려진 제한사항**:
  - 게시글 본문(`content`)은 서버 검증 없이 HTML로 저장·렌더링(`v-html`) — **XSS 위험**, 실서비스 전 DOMPurify 등 sanitizer 적용 필요(현재 리치 에디터가 아닌 textarea로 임시 구현되어 있어 일반 사용자가 스크립트 태그를 직접 넣을 경우 위험도 높음)
  - 조회수 중복 방지 로직 없음(Review 권고사항, 미반영)
  - 이미지 업로드 API 미구현(Designer 명세의 드래그앤드롭 업로드는 다음 이터레이션)
- **특별히 검증이 필요한 엣지 케이스**: 비공개/이웃공개 게시글 접근 권한, 좋아요 연타(중복 카운트 여부), 대댓글 depth 제한, 비밀번호 규칙 검증, 리프레시 토큰 만료 후 자동 재시도 동작

## 🎨 Designer Agent 산출물

### 컨셉
**"물빛 다이어리"** — 수채화 붓터치의 부드러운 배경 + 웹툰 말풍선/컷 분할 감성의 콘텐츠 카드.
텍스트 가독성은 일반 블로그 수준을 유지하되, 배경·아이콘·강조 요소에 수채화 번짐과 웹툰식 굵은 윤곽선을 사용한다.

### 1. 화면 목록 및 플로우
```
[로그인] → [회원가입]
[로그인] → [블로그 홈(피드)] → [게시글 상세(댓글/공감)]
[블로그 홈] → [글쓰기(에디터)] → [게시글 상세]
[블로그 홈] → [카테고리/태그 필터]
[블로그 홈] → [내 정보 수정]
```

### 2. 디자인 시스템

#### 컬러 토큰 (수채화 파스텔 팔레트)
| 토큰 | 값 | 용도 |
|---|---|---|
| `--color-bg` | `#FBF7F0` | 기본 배경 (한지톤 아이보리) |
| `--color-bg-wash` | `radial-gradient(circle at 20% 20%, #E3F0FF 0%, transparent 45%), radial-gradient(circle at 80% 70%, #FFE9E3 0%, transparent 45%)` | 배경 수채화 번짐 레이어 |
| `--color-primary` | `#5FA8D3` | 하늘빛 블루 (주 액션) |
| `--color-primary-dark` | `#3E7CA6` | 버튼 hover/active |
| `--color-secondary` | `#F4A6A0` | 산호빛 코랄 (좋아요/포인트) |
| `--color-accent-yellow` | `#F7D774` | 태그/하이라이트 |
| `--color-ink` | `#2B2B2B` | 본문 텍스트 (웹툰 잉크선 연상) |
| `--color-ink-soft` | `#6B6B6B` | 보조 텍스트 |
| `--color-error` | `#E4574C` | 에러/경고 |
| `--color-surface` | `#FFFFFF` | 카드/말풍선 배경 |
| `--color-border` | `#2B2B2B` | 웹툰풍 굵은 아웃라인 (2px) |

#### 다크모드
- `--color-bg`→`#1E2126`, `--color-surface`→`#2A2E35`, `--color-ink`→`#F2F0EA`, 수채화 wash는 opacity 0.15로 낮춰 은은하게 유지. 아웃라인은 `--color-border`→`#F2F0EA` 저채도로 전환.

#### 타이포그래피
- Heading: `'Gaegu', 'Jua', sans-serif` (손글씨풍 — 웹툰 말풍선 감성), 굵기 700
- Body: `'Pretendard', system-ui, sans-serif`, 가독성 우선, 굵기 400/500
- 크기 스케일: 12 / 14 / 16(base) / 20 / 24 / 32 / 40px

#### 간격 시스템
- 기본 단위 8px (8/16/24/32/48/64)
- 카드 내부 padding: 24px, 카드 간 gap: 24px

#### 형태 언어
- 카드/버튼 모서리: `border-radius: 18px` (손그림 느낌의 비정형에 가까운 라운드)
- 테두리: 2px solid `--color-border`, box-shadow로 살짝 어긋난 느낌(`3px 3px 0 rgba(43,43,43,0.15)`) 부여해 웹툰 컷 느낌
- 버튼: 눌렀을 때 translate(2px,2px) + shadow 제거 → 종이/컷 눌리는 느낌
- 아이콘: 손그림풍 outline 아이콘(굵기 2px), 좋아요는 하트에 수채화 번짐 텍스처

#### 브레이크포인트
- 360px(모바일) / 768px(태블릿) / 1280px(데스크톱), Mobile-first

### 3. 컴포넌트 목록
- [ ] `AppButton`: props(`variant`: primary/secondary/ghost, `size`, `loading`) — 눌림 애니메이션 포함
- [ ] `WatercolorCard`: 게시글/댓글 공용 카드 컨테이너, slot 기반
- [ ] `SpeechBubbleComment`: 댓글 전용 — 말풍선 꼬리 CSS, 대댓글은 들여쓰기 + 꼬리 좌측 이동
- [ ] `TagChip`: 태그 표시, `--color-accent-yellow` 배경 + 손글씨 폰트
- [ ] `CategoryTree`: 계층형 카테고리 사이드 메뉴, 펼침/접힘 상태(state)
- [ ] `LikeHeart`: 공감 버튼, 클릭 시 수채화 번짐 파티클 애니메이션 + 카운트, 토글 상태(state)
- [ ] `PostCard`: 피드용 게시글 미리보기 카드 (썸네일/제목/요약/태그/좋아요수/댓글수)
- [ ] `RichEditor`: 게시글 작성 에디터 래퍼, 이미지 업로드 드래그앤드롭 상태 표시
- [ ] `Avatar`: 프로필 이미지, 손그림풍 원형 프레임(이중 테두리)
- [ ] `EmptyState` / `LoadingState` / `ErrorState`: 공용 상태 컴포넌트, 각각 수채화 일러스트 자리 표시

### 4. 화면별 UI 명세

상세 명세는 [screens/](screens/) 디렉토리 참조:
- [login-signup.md](screens/login-signup.md)
- [blog-home.md](screens/blog-home.md)
- [post-detail.md](screens/post-detail.md)
- [post-editor.md](screens/post-editor.md)

### 📤 Dev에게 전달할 사항
- **컴포넌트 구현 우선순위**: `AppButton` → `WatercolorCard`/`PostCard` → `SpeechBubbleComment` → `LikeHeart` → `RichEditor` 순
- **반응형 분기점**: 360 / 768 / 1280px, `PostCard`는 모바일 1열 → 태블릿 2열 → 데스크톱 3열 그리드
- **접근성 준수 항목**: 색 대비 AA 이상 확보(파스텔 배경 위 텍스트는 반드시 `--color-ink` 사용, 컬러만으로 상태 구분 금지 — 좋아요는 하트 채움+카운트 동시 표기), 모든 인터랙션 요소 `:focus-visible` 아웃라인 유지, 이미지 `alt` 필수
- CSS 변수(`:root`)로 토큰 정의 후 컴포넌트에서 `var(--color-*)` 참조 — 다크모드는 `[data-theme="dark"]` 오버라이드

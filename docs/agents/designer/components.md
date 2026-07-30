# 컴포넌트 명세 (Vue 3 SFC 기준)

## AppButton
- Props: `variant: 'primary'|'secondary'|'ghost'`, `size: 'sm'|'md'|'lg'`, `loading: boolean`, `disabled: boolean`
- State: hover(살짝 떠오름 -2px translateY), active(눌림 +2px translate + shadow 제거), loading(스피너 아이콘)
- Style: 2px solid `--color-border`, radius 18px, shadow `3px 3px 0 rgba(43,43,43,.15)`

## WatercolorCard
- Slots: `header`, `default`, `footer`
- Props: `tint: 'blue'|'coral'|'yellow'|'none'` — 배경에 은은한 수채화 wash 적용
- Style: `--color-surface` 배경, radius 18px, border 2px, hover 시 shadow 확대

## PostCard
- Props: `post: { id, title, summary, thumbnailUrl, tags[], likeCount, commentCount, viewCount, categoryName, publishedAt }`
- Layout: 썸네일(16:9, 수채화 프레임 마스크) → 제목(Heading font) → 요약 2줄 ellipsis → TagChip 리스트 → 하단 메타(좋아요/댓글/조회수 아이콘+숫자)
- Edge case: 썸네일 없음 → 카테고리 색상의 그라디언트 placeholder

## SpeechBubbleComment
- Props: `comment: { id, author, content, createdAt, isSecret, depth }`
- 최상위 댓글: 좌측 정렬 말풍선(꼬리 좌상단), 대댓글(depth=1): 32px 들여쓰기 + 꼬리 위치 반전
- 비밀댓글 & 본인/작성자 아님: 내용 대신 "🔒 비밀 댓글입니다" 표시
- 삭제된 댓글(대댓글 존재): "삭제된 댓글입니다" 회색 처리, 답글 유지

## LikeHeart
- Props: `liked: boolean`, `count: number`
- Emits: `toggle`
- 클릭 시: 채워진 하트로 전환 + 반경 24px 수채화 파티클 튐 애니메이션(0.4s), count +1/-1 즉시 반영(낙관적 업데이트) 후 API 실패 시 롤백

## TagChip
- Props: `label: string`, `clickable: boolean`
- Style: `--color-accent-yellow` 배경, radius 999px(캡슐), 손글씨 폰트 14px

## CategoryTree
- Props: `categories: [{id, name, parentId, postCount}]`, `activeId`
- 최대 2 depth 렌더링, 하위 카테고리는 펼침(chevron) 토글

## RichEditor
- Props: `modelValue: string(HTML)`, `images: File[]`
- 이미지 드래그앤드롭 시 업로드 진행 표시(WatercolorCard 위에 progress ring), 실패 시 ErrorState 인라인 표시
- 발행 전 상태: DRAFT 자동저장(30초 간격, 로컬스토리지 fallback)

## Avatar
- Props: `src`, `size: 'sm'|'md'|'lg'`
- 이중 원형 테두리(바깥 `--color-accent-yellow`, 안쪽 `--color-border`)

## EmptyState / LoadingState / ErrorState
- Props: `message`, `illustration: 'post'|'comment'|'search'|'network'`
- 공통: 중앙 정렬, 일러스트(수채화 톤 SVG placeholder) + 메시지 + (선택)재시도 버튼

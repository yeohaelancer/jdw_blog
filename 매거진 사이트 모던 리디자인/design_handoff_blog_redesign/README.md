# Handoff: 물빛 다이어리 블로그 모던 리디자인

## Overview
기존 낚시 정보 블로그("물빛 다이어리")의 목록/상세 페이지를 트렌디한 매거진 스타일로 리디자인한 결과물. 다크모드, 인기글 섹션, 리스트/매거진형 카드 레이아웃을 새로 도입.

## About the Design Files
이 폴더의 `물빛 다이어리.dc.html`은 **디자인 레퍼런스로 만든 HTML 프로토타입**이며, 실제 서비스 코드로 그대로 복사해 붙여넣는 용도가 아닙니다. Claude Code(또는 담당 개발자)는 이 파일을 참고해 **기존 코드베이스의 프레임워크/스타일 시스템(React, Vue, 기존 블로그 템플릿 엔진 등)에 맞게 동일한 디자인을 재구현**해야 합니다. 기존 프레임워크가 없다면 프로젝트에 가장 적합한 스택을 선택해 구현하면 됩니다.

## Fidelity
**High-fidelity.** 색상(oklch 값), 타이포그래피, 간격, 카드 레이아웃, 다크모드 토큰까지 최종 값으로 지정되어 있음. 개발자는 기존 코드베이스의 컴포넌트/라이브러리를 사용해 픽셀 단위로 동일하게 재현하면 됨.

## Screens / Views

### 1. 목록 페이지 (List View)
- **Purpose**: 전체 글 목록, 인기글 확인, 카테고리/태그로 탐색
- **Layout**:
  - 상단 sticky 헤더: 좌측 로고("물빛 다이어리"), 우측 nav(전체 글/인기글/태그) + 다크모드 토글 스위치 + "글쓰기" 버튼. `padding: 18px 40px`, `border-bottom: 1px solid`
  - Hero: 중앙 정렬 타이틀(40px, weight 800) + 서브타이틀. `padding: 56px 40px 36px`
  - 인기글 섹션: `max-width:1160px` 컨테이너, 3열 그리드(`grid-template-columns: repeat(3,1fr)`, `gap:20px`), 각 카드에 순위 배지(좌상단 원형, accent 배경)
  - 본문: 2열 그리드(`220px 1fr`, `gap:40px`) — 좌측 사이드바(카테고리 목록 + 태그 칩), 우측 메인
  - 메인 첫 글은 "피처드" 레이아웃(2열, 이미지+텍스트 나란히, `grid-template-columns:1.1fr 1fr`), 나머지는 리스트 행(`120px 1fr` 썸네일+텍스트, 구분선 `border-bottom`)
- **Components**:
  - 카드/행 클릭 시 상세 페이지로 전환
  - 다크모드 토글: 44×24px 트랙 + 18px 원형 노브, `left` 값으로 애니메이션
  - 태그 칩: `border-radius:9999px`, accent-soft 배경

### 2. 상세 페이지 (Detail View)
- **Purpose**: 개별 글 전체 내용 열람
- **Layout**: `max-width:760px` 중앙 정렬, `padding:48px 40px 100px`
- **Components** (순서대로):
  1. "← 목록으로" 텍스트 버튼
  2. 카테고리·태그 라벨 → 제목(32px, weight 800) → 메타 정보(날짜/조회/댓글/좋아요)
  3. 대표 이미지 자리(360px 높이 placeholder)
  4. 인트로 본문
  5. "포인트 선정 기준" 4개 항목 2열 그리드
  6. "검증 포인트 10곳" 번호 리스트(원형 인덱스 배지 + 이름 + 설명)
  7. 마무리 본문
  8. 태그 칩
  9. "다른 인기글" 3열 관련글 카드

## Interactions & Behavior
- 카드/행 클릭 → 상세 페이지로 전환, 스크롤 최상단 이동
- 로고 또는 "전체 글" 클릭 → 목록으로 복귀
- 다크모드 토글 클릭 → 전체 색상 토큰 전환(트랜지션 0.2s)
- 관련글/인기글 카드 클릭 → 해당 글 상세로 전환

## State Management
- `view`: 'list' | 'detail'
- `selectedId`: 현재 보고 있는 글 id
- `darkMode`: boolean
- 게시글 데이터는 배열(id, region, tag, title, excerpt, views, likes, comments, date)로 관리, 상세 진입 시 본문(intro/spots/closing)을 생성

## Design Tokens

### Light
- bgPage: oklch(97% 0.006 240) / bgSurface: oklch(99.5% 0.002 240)
- textPrimary: oklch(22% 0.02 250) / textMuted: oklch(50% 0.02 250)
- border: oklch(91% 0.008 250)
- accent: oklch(55% 0.16 240) / accentSoft: oklch(93% 0.04 240) / accentSoftText: oklch(40% 0.13 240)
- cardShadow: 0 1px 3px rgba(20,30,50,0.06)

### Dark
- bgPage: oklch(19% 0.014 250) / bgSurface: oklch(24% 0.016 250)
- textPrimary: oklch(93% 0.01 250) / textMuted: oklch(66% 0.02 250)
- border: oklch(33% 0.02 250)
- accent: oklch(72% 0.14 240) / accentSoft: oklch(32% 0.06 240) / accentSoftText: oklch(85% 0.06 240)
- cardShadow: none

### Typography
- 폰트: Pretendard, fallback -apple-system/Malgun Gothic/sans-serif
- 타이틀 38-40px/weight 800, 상세 제목 32px/800, 카드 제목 15-17px/700, 본문 14-16px/400-500, 메타 12-13px

### Radius / Spacing
- 카드 radius 14-20px, 칩 radius 9999px, 컨테이너 max-width 1160px(목록)/760px(상세)

## Assets
- 이미지 자리(사진 슬롯)는 실제 사진이 없어 diagonal-stripe 패턴 placeholder + monospace 라벨로 표시됨. 실제 사진 에셋으로 교체 필요.

## Files
- `물빛 다이어리.dc.html` — 목록/상세 페이지 전체 디자인 (단일 파일, 인라인 스타일)

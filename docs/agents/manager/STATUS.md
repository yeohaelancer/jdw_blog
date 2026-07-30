# 프로젝트 진행 현황 (jdw_blog)

## 파이프라인
Manager → (DBA + Designer 병렬) → Review → Dev → QA → DevOps

## 단계별 상태

| 단계 | 상태 | 산출물 |
|---|---|---|
| Manager | ✅ 완료 | 본 문서 |
| DBA | ✅ 완료 | [docs/agents/dba](../dba/README.md) |
| Designer | ✅ 완료 | [docs/agents/designer](../designer/design-system.md) |
| Review | ✅ 완료 | [docs/agents/review](../review/REVIEW_RESULT.md) |
| Dev | ✅ 완료 | [docs/agents/dev](../dev/IMPLEMENTATION.md) |
| QA | ✅ 완료 | [docs/agents/qa](../qa/TEST_CASES.md) |
| DevOps | ✅ 완료 | [docs/agents/devops](../devops/INFRA.md) |

## 이번 이터레이션 범위
- 핵심 기능: 회원가입/로그인(JWT), 게시글 CRUD, 댓글/대댓글, 공감(좋아요)
- 디자인 컨셉: 수채화(watercolor) 톤 + 웹툰(webtoon) 감성
- 범위 밖(다음 이터레이션): 이웃/서로이웃 UI, 관리자 대시보드, 소셜 로그인, 예약 발행
  (단, DB 스키마는 `blog_neighbor`, `blog_report` 등으로 이미 준비됨)

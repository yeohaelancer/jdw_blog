## 🚀 DevOps Agent 산출물

### 인프라 아키텍처
- 배포 환경: dev(로컬 `npm run dev` + `gradlew bootRun`) / staging·production(Docker Compose)
- 주요 컴포넌트: MySQL 8.0(`blog_` 스키마) ← Spring Boot 백엔드(8080) ← Nginx(80, 정적 파일 서빙 + `/api` 리버스 프록시) ← 프론트엔드(Vue 3 SPA)

### Docker 설정
- [backend/Dockerfile](../../../backend/Dockerfile): `gradle:8.10-jdk17`로 `bootJar` 빌드 → `eclipse-temurin:17-jre-jammy` 런타임(non-root 사용자)
- [frontend/Dockerfile](../../../frontend/Dockerfile): `node:20-alpine`로 `npm run build` → `nginx:1.27-alpine`에 정적 파일 서빙(빌드 컨텍스트는 저장소 루트 기준)
- [nginx/nginx.docker.conf](../../../nginx/nginx.docker.conf): compose 네트워크 내 `backend` 서비스명으로 `/api` 프록시 (bare-metal 배포용 샘플은 기존 [nginx/nginx.conf](../../../nginx/nginx.conf) 유지)
- [docker-compose.yml](../../../docker-compose.yml): `mysql`(스키마 자동 초기화, healthcheck) → `backend`(healthcheck 통과 후 기동) → `frontend`(nginx) 3-tier 구성, `mysql-data` 볼륨으로 영속화

### CI/CD 파이프라인
[.github/workflows/ci.yml](../../../.github/workflows/ci.yml): backend(JDK 17 + `./gradlew build`)와 frontend(Node 20 + `npm ci && npm run lint && npm run build`)를 병렬 job으로 실행. `main`/`develop` push 및 PR에서 트리거.

> **DevOps 작업 중 발견/보완한 실제 이슈**: 저장소에 `gradle-wrapper.jar`/`gradlew`가 누락되어 있어 CI의 `./gradlew build`가 동작 불가능한 상태였음 → Gradle 8.10으로 `gradle wrapper` 태스크를 실행해 wrapper 일체를 생성·커밋 대상에 포함시킴(빌드 검증용으로 임시 다운로드했던 Gradle은 스크래치 디렉터리에만 존재, 프로젝트에는 wrapper 산출물만 반영).

### 환경 변수 관리
- 루트: [.env.example](../../../.env.example) — `docker-compose.yml`용(`MYSQL_PASSWORD`, `MYSQL_ROOT_PASSWORD`, `JWT_SECRET`)
- 백엔드: `backend/.env.example` — 로컬 직접 실행용(DB 4종 전환 + JWT 설정), 실제 값은 `backend/.env`(gitignore 대상)에 이미 `jdworks` MySQL 기준으로 구성되어 있음(DBA 산출물 연동 확인 완료)
- **시크릿은 코드에 하드코딩하지 않음** — `.env`는 루트/backend 모두 `.gitignore`에 등록됨. `docker-compose.yml`은 `${VAR:?...}` 문법으로 필수 환경변수 누락 시 기동을 명시적으로 실패시킴

### 모니터링 설정
- 헬스체크 엔드포인트: `GET /api/health` (Spring Boot, `docker-compose.yml`의 backend healthcheck에서 사용), MySQL은 `mysqladmin ping`
- 알림 조건(권고): 헬스체크 3회 연속 실패 시 알림, 5xx 응답률 급증 시 알림 — 실제 알림 채널(Slack/이메일 등) 연동은 인프라 계정 준비 후 후속 작업

### 배포 체크리스트
- [ ] `.env` 파일에 실제 운영용 `MYSQL_PASSWORD`/`MYSQL_ROOT_PASSWORD`/`JWT_SECRET` 설정(예제 값 사용 금지)
- [x] DB 마이그레이션: `docs/agents/dba/schema.sql`이 `docker-entrypoint-initdb.d`로 자동 실행되도록 구성됨(최초 볼륨 생성 시 1회만 실행되는 MySQL 공식 이미지 동작 방식 숙지 필요 — 이미 데이터가 있는 볼륨 재사용 시 스키마 변경분은 별도 마이그레이션 필요)
- [ ] 헬스체크 통과 확인 (`docker compose ps`로 backend/mysql healthy 상태 확인)
- [ ] 롤백 플랜: 이미지 태그를 이전 버전으로 되돌려 `docker compose up -d`로 재기동(볼륨은 유지되므로 DB는 별도 백업/복구 전략 필요 — 현재 볼륨 백업 자동화는 미구성, 후속 작업 권고)

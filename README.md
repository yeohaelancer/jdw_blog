# jdw_blog (Spring Boot + Vue.js)

여러 프로젝트에서 재사용 가능한 기본 개발 셋입니다.

## 기술 스택

| 영역 | 기술 |
|---|---|
| Backend | Java 17, Spring Boot 3.3, Gradle |
| ORM | MyBatis 3 |
| Logging | Log4j2 |
| API 문서 | Swagger (springdoc-openapi 2.6) |
| Frontend | Vue.js 3 (Vite), Vue Router, Pinia, Axios |
| Web Server | Nginx (정적 서빙 + API 리버스 프록시) |
| DB | Oracle / MSSQL / MySQL / PostgreSQL 중 선택 (.env로 전환) |

## 폴더 구조

```
jdw_blog/
├── backend/            Spring Boot API 서버 (독립 실행)
│   ├── build.gradle
│   ├── .env.example    DB 접속 정보 예시 (4개 DB 템플릿 포함)
│   ├── sql/             DB별 샘플 테이블 생성 스크립트
│   └── src/main/...
├── frontend/           Vue.js 3 SPA (독립 실행)
│   ├── package.json
│   ├── .env.example
│   └── src/...
└── nginx/
    └── nginx.conf      운영 배포 시 참고용 리버스 프록시 설정
```

백엔드/프론트엔드는 완전히 분리된 프로젝트이며, 각자 독립적으로 빌드·배포합니다.

## 1. Backend 실행 방법

```bash
cd backend
cp .env.example .env
```

`.env` 파일을 열어 사용할 DB 블록 하나만 남기고 나머지는 주석 처리하세요.
(기본값은 PostgreSQL로 설정되어 있습니다)

```env
DB_DRIVER=org.postgresql.Driver
DB_URL=jdbc:postgresql://localhost:5432/basedb
DB_USERNAME=postgres
DB_PASSWORD=postgres
```

`sql/schema_{db종류}.sql` 스크립트로 샘플 테이블을 먼저 생성한 뒤 서버를 실행하세요.

```bash
# gradle wrapper가 없는 경우 최초 1회 생성 (로컬에 Gradle 8.10 설치되어 있어야 함)
gradle wrapper --gradle-version 8.10

./gradlew bootRun
```

기본 포트: `http://localhost:8080` (`/api/health` 로 헬스체크 가능)

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI 스펙(JSON): `http://localhost:8080/v3/api-docs`

### DB 전환 시 참고사항
- MySQL / PostgreSQL / MSSQL : 코드 수정 없이 `.env`만 교체하면 그대로 동작합니다.
- Oracle : PK 자동증가 방식이 시퀀스+트리거 기반이라 `SampleMapper.xml`의 insert 구문을 Oracle용으로 교체해야 합니다. (`sql/schema_oracle.sql` 하단 주석 참고)
- 새 DB 드라이버가 필요 없다면 `build.gradle`의 `runtimeOnly` 4줄 중 사용하지 않는 드라이버는 지워도 됩니다.

## 2. Frontend 실행 방법

```bash
cd frontend
cp .env.example .env
npm install
npm run dev
```

기본 포트: `http://localhost:5173` (dev 서버가 `/api` 요청을 백엔드 8080으로 프록시)

배포용 빌드:
```bash
npm run build   # frontend/dist 생성
```

## 3. Nginx (운영 배포 시)

`nginx/nginx.conf` 파일에서:
1. `root` 경로를 `frontend/dist`의 실제 절대경로로 수정
2. `proxy_pass` 대상을 백엔드 실제 접속 주소로 수정
3. 서버에 해당 설정을 등록 후 nginx 재시작

## 4. 새 프로젝트에 적용할 때 체크리스트

- [ ] `backend/build.gradle` 의 `group` 값 프로젝트에 맞게 변경
- [ ] `com.base.app` 패키지명 전체 rename (프로젝트명에 맞게)
- [ ] `backend/settings.gradle` 의 `rootProject.name` 변경
- [ ] `frontend/package.json` 의 `name` 변경
- [ ] `.env.example` → `.env` 로 복사 후 실제 DB 접속정보 입력 (`.env`는 git에 커밋되지 않음)
- [ ] 샘플 코드(`Sample*`)는 실제 도메인 코드로 교체/삭제
- [ ] `nginx.conf` 의 경로/도메인 수정

## 5. 샘플 CRUD API (참고용, 삭제 가능)

| Method | URL | 설명 |
|---|---|---|
| GET | /api/samples | 목록 조회 |
| GET | /api/samples/{id} | 단건 조회 |
| POST | /api/samples | 등록 |
| PUT | /api/samples/{id} | 수정 |
| DELETE | /api/samples/{id} | 삭제 |

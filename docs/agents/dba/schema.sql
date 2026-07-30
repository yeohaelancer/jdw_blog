-- =========================================================
-- jdw_blog DDL (MySQL 8.0)
-- prefix: blog_
-- 3NF 정규화 / BIGINT UNSIGNED PK / created_at,updated_at,deleted_at(soft delete) 공통 적용
-- =========================================================

-- 1. 회원
CREATE TABLE blog_user (
    id                  BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    email               VARCHAR(191)    NOT NULL,
    password            VARCHAR(255)    NOT NULL COMMENT 'BCrypt 해시 저장 (평문 저장 금지)',
    nickname            VARCHAR(50)     NOT NULL,
    profile_image_url   VARCHAR(500)    NULL,
    role                VARCHAR(20)     NOT NULL DEFAULT 'USER' COMMENT 'USER, ADMIN',
    status              VARCHAR(20)     NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE, SUSPENDED, WITHDRAWN',
    social_provider     VARCHAR(20)     NULL COMMENT 'GOOGLE, KAKAO / NULL=일반가입',
    social_id           VARCHAR(100)    NULL,
    created_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at          DATETIME        NULL,
    UNIQUE KEY uk_blog_user_email (email),
    UNIQUE KEY uk_blog_user_social (social_provider, social_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Refresh Token
CREATE TABLE blog_refresh_token (
    id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT UNSIGNED NOT NULL,
    token       VARCHAR(500)    NOT NULL COMMENT '해시 저장 권고',
    expires_at  DATETIME        NOT NULL,
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at  DATETIME        NULL,
    KEY idx_blog_refresh_token_user (user_id),
    CONSTRAINT fk_blog_refresh_token_user FOREIGN KEY (user_id) REFERENCES blog_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. 블로그 (회원 1:1)
CREATE TABLE blog_blog (
    id                    BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id               BIGINT UNSIGNED NOT NULL,
    blog_name             VARCHAR(100)    NOT NULL,
    blog_url_slug         VARCHAR(100)    NOT NULL,
    intro                 TEXT            NULL,
    profile_image_url     VARCHAR(500)    NULL,
    cover_image_url       VARCHAR(500)    NULL,
    skin_theme            VARCHAR(50)     NOT NULL DEFAULT 'basic',
    visitor_count_today   BIGINT UNSIGNED NOT NULL DEFAULT 0,
    total_visitor_count   BIGINT UNSIGNED NOT NULL DEFAULT 0,
    created_at            DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at            DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at            DATETIME        NULL,
    UNIQUE KEY uk_blog_blog_user (user_id),
    UNIQUE KEY uk_blog_blog_slug (blog_url_slug),
    CONSTRAINT fk_blog_blog_user FOREIGN KEY (user_id) REFERENCES blog_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. 이웃/팔로우
CREATE TABLE blog_neighbor (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    from_blog_id    BIGINT UNSIGNED NOT NULL COMMENT '이웃 신청한 블로그',
    to_blog_id      BIGINT UNSIGNED NOT NULL COMMENT '이웃 대상 블로그',
    neighbor_type   VARCHAR(20)     NOT NULL DEFAULT 'ONE_SIDED' COMMENT 'ONE_SIDED, MUTUAL_PENDING, MUTUAL',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at      DATETIME        NULL,
    UNIQUE KEY uk_blog_neighbor (from_blog_id, to_blog_id),
    KEY idx_blog_neighbor_to (to_blog_id),
    CONSTRAINT fk_blog_neighbor_from FOREIGN KEY (from_blog_id) REFERENCES blog_blog(id),
    CONSTRAINT fk_blog_neighbor_to   FOREIGN KEY (to_blog_id)   REFERENCES blog_blog(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. 카테고리 (계층형)
CREATE TABLE blog_category (
    id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    blog_id     BIGINT UNSIGNED NOT NULL,
    parent_id   BIGINT UNSIGNED NULL COMMENT 'NULL=최상위 카테고리',
    name        VARCHAR(100)    NOT NULL,
    sort_order  INT             NOT NULL DEFAULT 0,
    post_count  INT UNSIGNED    NOT NULL DEFAULT 0,
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at  DATETIME        NULL,
    KEY idx_blog_category_blog (blog_id),
    KEY idx_blog_category_parent (parent_id),
    CONSTRAINT fk_blog_category_blog   FOREIGN KEY (blog_id)   REFERENCES blog_blog(id),
    CONSTRAINT fk_blog_category_parent FOREIGN KEY (parent_id) REFERENCES blog_category(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. 게시글
CREATE TABLE blog_post (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    blog_id         BIGINT UNSIGNED NOT NULL,
    category_id     BIGINT UNSIGNED NULL,
    title           VARCHAR(200)    NOT NULL,
    content         LONGTEXT        NOT NULL,
    thumbnail_url   VARCHAR(500)    NULL,
    visibility      VARCHAR(20)     NOT NULL DEFAULT 'PUBLIC' COMMENT 'PUBLIC, NEIGHBOR, PRIVATE',
    status          VARCHAR(20)     NOT NULL DEFAULT 'PUBLISHED' COMMENT 'DRAFT, PUBLISHED, SCHEDULED',
    published_at    DATETIME        NULL,
    view_count      BIGINT UNSIGNED NOT NULL DEFAULT 0,
    like_count      BIGINT UNSIGNED NOT NULL DEFAULT 0,
    comment_count   BIGINT UNSIGNED NOT NULL DEFAULT 0,
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at      DATETIME        NULL,
    KEY idx_blog_post_blog (blog_id),
    KEY idx_blog_post_category (category_id),
    KEY idx_blog_post_published (status, visibility, published_at),
    FULLTEXT KEY ft_blog_post_title_content (title, content),
    CONSTRAINT fk_blog_post_blog     FOREIGN KEY (blog_id)     REFERENCES blog_blog(id),
    CONSTRAINT fk_blog_post_category FOREIGN KEY (category_id) REFERENCES blog_category(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7. 게시글 첨부 이미지
CREATE TABLE blog_post_image (
    id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    post_id     BIGINT UNSIGNED NOT NULL,
    image_url   VARCHAR(500)    NOT NULL,
    sort_order  INT             NOT NULL DEFAULT 0,
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at  DATETIME        NULL,
    KEY idx_blog_post_image_post (post_id),
    CONSTRAINT fk_blog_post_image_post FOREIGN KEY (post_id) REFERENCES blog_post(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 8. 태그
CREATE TABLE blog_tag (
    id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(50)     NOT NULL,
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at  DATETIME        NULL,
    UNIQUE KEY uk_blog_tag_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 9. 게시글-태그 매핑 (N:M)
CREATE TABLE blog_post_tag (
    id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    post_id     BIGINT UNSIGNED NOT NULL,
    tag_id      BIGINT UNSIGNED NOT NULL,
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_blog_post_tag (post_id, tag_id),
    KEY idx_blog_post_tag_tag (tag_id),
    CONSTRAINT fk_blog_post_tag_post FOREIGN KEY (post_id) REFERENCES blog_post(id),
    CONSTRAINT fk_blog_post_tag_tag  FOREIGN KEY (tag_id)  REFERENCES blog_tag(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 10. 댓글 (대댓글 포함)
CREATE TABLE blog_comment (
    id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    post_id     BIGINT UNSIGNED NOT NULL,
    user_id     BIGINT UNSIGNED NOT NULL,
    parent_id   BIGINT UNSIGNED NULL COMMENT 'NULL=최상위 댓글, NOT NULL=대댓글',
    content     VARCHAR(1000)   NOT NULL,
    is_secret   TINYINT(1)      NOT NULL DEFAULT 0,
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at  DATETIME        NULL COMMENT 'soft delete, 대댓글 존재 시 "삭제된 댓글"로 표시 처리',
    KEY idx_blog_comment_post (post_id),
    KEY idx_blog_comment_user (user_id),
    KEY idx_blog_comment_parent (parent_id),
    CONSTRAINT fk_blog_comment_post   FOREIGN KEY (post_id)   REFERENCES blog_post(id),
    CONSTRAINT fk_blog_comment_user   FOREIGN KEY (user_id)   REFERENCES blog_user(id),
    CONSTRAINT fk_blog_comment_parent FOREIGN KEY (parent_id) REFERENCES blog_comment(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 11. 게시글 공감(좋아요)
CREATE TABLE blog_post_like (
    id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    post_id     BIGINT UNSIGNED NOT NULL,
    user_id     BIGINT UNSIGNED NOT NULL,
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at  DATETIME        NULL COMMENT '좋아요 취소 시 soft delete (토글 이력 보존)',
    UNIQUE KEY uk_blog_post_like (post_id, user_id),
    KEY idx_blog_post_like_user (user_id),
    CONSTRAINT fk_blog_post_like_post FOREIGN KEY (post_id) REFERENCES blog_post(id),
    CONSTRAINT fk_blog_post_like_user FOREIGN KEY (user_id) REFERENCES blog_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 12. 방문자 로그 (일별 집계 기반)
CREATE TABLE blog_visitor_log (
    id                BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    blog_id           BIGINT UNSIGNED NOT NULL,
    visitor_ip        VARCHAR(45)     NOT NULL COMMENT 'IPv4/IPv6, 개인정보 보호를 위해 마지막 옥텟 마스킹 권고',
    visitor_user_id   BIGINT UNSIGNED NULL COMMENT '비로그인 방문자는 NULL',
    visit_date        DATE            NOT NULL,
    created_at        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_blog_visitor_log_blog_date (blog_id, visit_date),
    CONSTRAINT fk_blog_visitor_log_blog FOREIGN KEY (blog_id)         REFERENCES blog_blog(id),
    CONSTRAINT fk_blog_visitor_log_user FOREIGN KEY (visitor_user_id) REFERENCES blog_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 13. 신고/제재 (관리자 확장 기능)
CREATE TABLE blog_report (
    id                  BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    reporter_user_id    BIGINT UNSIGNED NOT NULL,
    target_type         VARCHAR(20)     NOT NULL COMMENT 'POST, COMMENT, USER',
    target_id           BIGINT UNSIGNED NOT NULL,
    reason              VARCHAR(500)    NOT NULL,
    status              VARCHAR(20)     NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING, REVIEWED, REJECTED, ACTIONED',
    handled_by_user_id  BIGINT UNSIGNED NULL COMMENT '처리한 관리자',
    handled_at          DATETIME        NULL,
    created_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at          DATETIME        NULL,
    KEY idx_blog_report_target (target_type, target_id),
    KEY idx_blog_report_status (status),
    CONSTRAINT fk_blog_report_reporter FOREIGN KEY (reporter_user_id)   REFERENCES blog_user(id),
    CONSTRAINT fk_blog_report_handler  FOREIGN KEY (handled_by_user_id) REFERENCES blog_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

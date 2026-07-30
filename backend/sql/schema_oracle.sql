CREATE TABLE sample (
    id          NUMBER(19) PRIMARY KEY,
    title       VARCHAR2(200) NOT NULL,
    content     CLOB,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE SEQUENCE seq_sample START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER trg_sample_id
BEFORE INSERT ON sample
FOR EACH ROW
BEGIN
    IF :NEW.id IS NULL THEN
        SELECT seq_sample.NEXTVAL INTO :NEW.id FROM DUAL;
    END IF;
END;
/

-- Oracle 사용 시 SampleMapper.xml의 insert 구문에서
-- useGeneratedKeys="true" 대신 아래처럼 selectKey를 사용하는 것을 권장합니다.
--
-- <insert id="insert" parameterType="com.base.app.dto.SampleDto">
--     <selectKey keyProperty="id" resultType="long" order="BEFORE">
--         SELECT seq_sample.NEXTVAL FROM DUAL
--     </selectKey>
--     INSERT INTO sample (id, title, content, created_at)
--     VALUES (#{id}, #{title}, #{content}, CURRENT_TIMESTAMP)
-- </insert>

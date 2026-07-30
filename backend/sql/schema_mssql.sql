CREATE TABLE sample (
    id          BIGINT IDENTITY(1,1) PRIMARY KEY,
    title       NVARCHAR(200) NOT NULL,
    content     NVARCHAR(MAX),
    created_at  DATETIME2 DEFAULT SYSDATETIME()
);

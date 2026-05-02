--liquibase formatted sql

--changeset jules:1
CREATE TABLE sample_entity (
    id BIGINT NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    created_by BIGINT,
    created_time TIMESTAMP,
    updated_by BIGINT,
    updated_time TIMESTAMP,
    tenant_id BIGINT
);
--rollback DROP TABLE sample_entity;

--changeset jules:2
INSERT INTO sample_entity (id, name, created_by, created_time, updated_by, updated_time, tenant_id)
VALUES (1, 'Initial Sample Data', NULL, CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, NULL);
--rollback DELETE FROM sample_entity WHERE id = 1;

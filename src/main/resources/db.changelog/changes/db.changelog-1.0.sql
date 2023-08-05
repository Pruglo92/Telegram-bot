--liquibase formatted sql

--changeset pruglo-ve:20230804-1 failOnError:true
--comment: Create notification_tasks table.
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:0 select count(*) from information_schema.tables where table_name = 'notification_tasks';
CREATE TABLE IF NOT EXISTS notification_tasks (
id              SERIAL      PRIMARY KEY,
chat_id         BIGINT      NOT NULL ,
text            TEXT        NOT NULL,
sent_date       TIMESTAMP   NOT NULL,
creation_date   TIMESTAMP
);
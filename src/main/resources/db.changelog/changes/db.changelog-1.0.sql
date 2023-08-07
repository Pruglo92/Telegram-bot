--liquibase formatted sql

--changeset pruglo-ve:20230804-1 failOnError:true
--comment: Create notification_tasks table.
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:0 select count(*) from information_schema.tables where table_name = 'notification_tasks';

CREATE TABLE IF NOT EXISTS notification_tasks (
id              BIGINT      PRIMARY KEY,
chat_id         BIGINT      NOT NULL ,
text            TEXT        NOT NULL,
sent_date       TIMESTAMP   NOT NULL,
creation_date   TIMESTAMP
);

--changeset pruglo-ve:20230806-2 failOnError:true
--comment: Create sequence notification_tasks_id_seq.
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:0 select count(*) from pg_sequences where schemaname = 'public' and sequencename = 'notification_tasks_id_seq';

CREATE SEQUENCE IF NOT EXISTS notification_tasks_id_seq;

--changeset pruglo-ve:20230806-3 failOnError:true
--comment: Alter table notification_tasks.
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:1 select count(*) from pg_sequences where schemaname = 'public' and sequencename = 'notification_tasks_id_seq';

ALTER TABLE notification_tasks
    ALTER COLUMN id SET DEFAULT nextval('notification_tasks_id_seq');
-- liquibase formatted sql

-- changeset slyubimov:1
create table schema_bot.notification_task_name
(
    id       serial,
    chat_id  bigint,
    user_id  bigint,
    deadline timestamp,
    task     varchar,
    constraint notification_task_name_pk
        primary key (id)
);
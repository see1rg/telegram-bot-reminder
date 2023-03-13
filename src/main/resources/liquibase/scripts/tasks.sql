-- liquibase formatted sql

-- changeset slyubimov:1
create table if not exists schema_bot.notification_task_name
(
    id       serial primary key,
    chat_id  bigint not null,
    user_id  bigint not null,
    deadline timestamp not null,
    task     varchar not null
);
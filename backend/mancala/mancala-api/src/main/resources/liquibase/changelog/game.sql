--liquibase formatted sql

--changeset Helvetto:create_game
create table game
(
    type varchar(31) not null,
    id   bigserial   not null,
    primary key (id)
)
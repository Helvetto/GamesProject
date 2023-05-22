--liquibase formatted sql

--changeset Helvetto:create_player
create table player
(
    id              bigserial not null,
    active_game_ids bigint[],
    primary key (id)
)
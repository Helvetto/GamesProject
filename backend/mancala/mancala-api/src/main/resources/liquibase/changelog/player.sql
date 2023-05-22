--liquibase formatted sql

--changeset Andrew:create_player
create table player
(
    id              bigserial not null,
    primary key (id)
);

COMMENT ON TABLE player IS 'This table represents a player.';
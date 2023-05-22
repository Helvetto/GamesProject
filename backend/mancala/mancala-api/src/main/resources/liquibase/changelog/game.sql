--liquibase formatted sql

--changeset Andrew:create_game
create table game
(
    type varchar(31) not null,
    id   bigserial   not null,
    primary key (id)
);

COMMENT ON TABLE game IS 'This table represents a base class for all games.';
COMMENT ON COLUMN game.type IS 'Type of the game, e.g., Mancala or Chess, etc.';

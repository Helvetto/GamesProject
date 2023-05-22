--liquibase formatted sql

--changeset Andrew:create_mancala_game
create table mancala_game
(
    id             bigserial not null,
    status         varchar(255),
    board_id       bigint,
    player_turn_id bigint,
    primary key (id)
);

alter table if exists mancala_game
    add constraint mancala_game_fk_to_board foreign key (board_id) references board;

alter table if exists mancala_game
    add constraint mancala_game_fk_to_player foreign key (player_turn_id) references player;

alter table if exists mancala_game
    add constraint mancala_game_fk_to_game foreign key (id) references game;

COMMENT ON TABLE mancala_game IS 'This table represents a Mancala game.';
COMMENT ON COLUMN mancala_game.status IS 'Current status of the game.';
COMMENT ON COLUMN mancala_game.board_id IS 'Link to the Mancala board.';
COMMENT ON COLUMN mancala_game.player_turn_id IS 'Current player turn.';

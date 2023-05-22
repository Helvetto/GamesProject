--liquibase formatted sql

--changeset Helvetto:create_mancala_game
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
--liquibase formatted sql

--changeset Andrew:create_player_active_games
create table player_active_games
(
    player_id bigint not null,
    game_id   bigint not null
);

alter table if exists player_active_games
    add constraint player_active_games_fk_to_player foreign key (player_id) references player;

alter table if exists player_active_games
    add constraint player_active_games_fk_to_game foreign key (game_id) references game;

create index if not exists player_active_games_player_id_idx on player_active_games (player_id);
create index if not exists player_active_games_game_id_idx on player_active_games (game_id);

COMMENT ON TABLE player_active_games IS 'This table represents the mapping between a player and their currently active games.';
COMMENT ON COLUMN player_active_games.player_id IS 'The ID of the player.';
COMMENT ON COLUMN player_active_games.game_id IS 'The ID of the active game.';
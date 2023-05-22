--liquibase formatted sql

--changeset Helvetto:create_game_to_player_mapping
create table game_to_player_mapping
(
    player_id bigint not null,
    game_id   bigint not null
);

alter table if exists game_to_player_mapping
    add constraint game_to_player_mapping_fk_to_player foreign key (player_id) references player;

alter table if exists game_to_player_mapping
    add constraint game_to_player_mapping_fk_to_game foreign key (game_id) references game
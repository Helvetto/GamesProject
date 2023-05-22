--liquibase formatted sql

--changeset Andrew:create_pit
create table pit
(
    id          bigserial not null,
    stones      integer,
    type        varchar(255),
    next_pit_id bigint,
    player_id   bigint,
    board_id    bigint,
    primary key (id)
);

alter table if exists pit
    add constraint pit_fk_to_itself foreign key (next_pit_id) references pit;

alter table if exists pit
    add constraint pit_fk_to_player foreign key (player_id) references player;

alter table if exists pit
    add constraint pit_fk_to_board foreign key (board_id) references board;

create index if not exists pit_next_pit_id_idx on pit (next_pit_id);

COMMENT ON TABLE pit IS 'This table represents a pit in a Mancala game.';
COMMENT ON COLUMN pit.stones IS 'The number of stones currently in the pit.';
COMMENT ON COLUMN pit.type IS 'The type of the pit, e.g., "MANCALA" or "NORMAL".';
COMMENT ON COLUMN pit.next_pit_id IS 'The ID of the next pit in the clockwise direction.';
COMMENT ON COLUMN pit.player_id IS 'The player who owns the pit.';
COMMENT ON COLUMN pit.board_id IS 'The ID of the board associated with the pit.';

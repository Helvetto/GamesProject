--liquibase formatted sql

--changeset Helvetto:create_pit
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
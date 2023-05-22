--liquibase formatted sql

--changeset Andrew:create_board
create table board
(
    id bigserial not null,
    primary key (id)
);

COMMENT ON TABLE board IS 'This table represents Mancala board';
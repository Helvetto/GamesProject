--liquibase formatted sql

--changeset Helvetto:create_board
create table board
(
    id bigserial not null,
    primary key (id)
)
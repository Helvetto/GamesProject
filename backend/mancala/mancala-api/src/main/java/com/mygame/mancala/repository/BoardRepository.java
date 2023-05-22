package com.mygame.mancala.repository;

import com.mygame.mancala.model.Board;
import com.mygame.repository.BaseRepository;

public interface BoardRepository extends BaseRepository<Board, Long> {

    @Override
    default Class<? super Board> getEntityClass() {
        return Board.class;
    }
}

package com.mygame.mancala.repository;

import com.mygame.mancala.model.MancalaGame;
import com.mygame.repository.BaseRepository;

public interface GameRepository extends BaseRepository<MancalaGame, Long> {
    @Override
    default Class<? super MancalaGame> getEntityClass() {
        return MancalaGame.class;
    }
}

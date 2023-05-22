package com.mygame.mancala.repository;

import com.mygame.mancala.model.pit.Pit;
import com.mygame.repository.BaseRepository;

public interface PitRepository extends BaseRepository<Pit, Long> {
    @Override
   default Class<? super Pit> getEntityClass() {
        return Pit.class;
    }
}

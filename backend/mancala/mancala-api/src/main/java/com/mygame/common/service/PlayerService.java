package com.mygame.common.service;

import java.util.Objects;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.PlayerRepository;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;

    @Transactional
    public void remove(Long playerId, Long gameId) {
        var player = playerRepository.findByIdOrThrow(playerId);
        var gamesToBeRemoved =
                player.getActiveGames().stream().filter(it -> Objects.equals(it.getId(), gameId)).toList();
        player.removeActiveGameIds(gamesToBeRemoved);
        playerRepository.save(player);
    }
}

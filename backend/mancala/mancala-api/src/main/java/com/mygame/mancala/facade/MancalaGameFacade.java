package com.mygame.mancala.facade;

import com.mygame.mancala.DTO.CreateMancalaGameParamsDto;
import com.mygame.mancala.DTO.MancalaGameDto;
import com.mygame.mancala.mapper.MancalaGameDtoMapper;
import com.mygame.mancala.repository.GameRepository;
import com.mygame.mancala.service.MancalaGameCreationService;
import com.mygame.mancala.service.MancalaGamePlayService;
import com.mygame.mancala.service.MancalaJoinGameService;
import com.mygame.mancala.service.MancalaUtilService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class MancalaGameFacade {

    private final MancalaGameCreationService gameCreationService;
    private final MancalaJoinGameService joinGameService;
    private final MancalaGamePlayService gamePlayService;
    private final MancalaGameDtoMapper mapper;
    private final GameRepository gameRepository;

    @Transactional
    public MancalaGameDto createAndJoinGame(CreateMancalaGameParamsDto dto, @Nullable Long playerId) {
        var createdGame = gameCreationService.createGame(dto);
        createdGame = joinGameService.joinGame(createdGame.getId(), playerId);
        return mapper.map(createdGame, createdGame.getPlayers());
    }

    @Transactional
    public MancalaGameDto joinGame(Long gameId, @Nullable Long playerId) {
        var existingPlayers = gameRepository.findByIdOrThrow(gameId).getPlayers();
        var game = joinGameService.joinGame(gameId, playerId);
        var newPlayers = MancalaUtilService.findNewPlayersAmongAll(existingPlayers, game.getPlayers());
        return mapper.map(game, newPlayers);
    }

    @Transactional
    public MancalaGameDto sow(Long gameId, Long pitId, Long playerId) {
        var game = gamePlayService.sow(gameId, pitId, playerId);
        return mapper.map(game);
    }

    public MancalaGameDto getInfo(Long gameId) {
        return mapper.map(gameRepository.findByIdOrThrow(gameId));
    }

}

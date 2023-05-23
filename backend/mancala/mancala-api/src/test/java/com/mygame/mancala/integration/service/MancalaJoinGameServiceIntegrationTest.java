package com.mygame.mancala.integration.service;

import com.mygame.exception.GameIsFullException;
import com.mygame.mancala.DTO.CreateMancalaGameParamsDto;
import com.mygame.mancala.integration.IntegrationTest;
import com.mygame.mancala.model.MancalaGameStatus;
import com.mygame.mancala.service.MancalaGameCreationService;
import com.mygame.mancala.service.MancalaJoinGameService;
import com.mygame.model.entity.Player;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import repository.PlayerRepository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RequiredArgsConstructor
public class MancalaJoinGameServiceIntegrationTest extends IntegrationTest {

    private final MancalaJoinGameService joinGameService;
    private final MancalaGameCreationService gameCreationService;
    private final PlayerRepository playerRepository;

    @Test
    void shouldJoinAnEmptyGameWithCreatedUser() {
        var player = playerRepository.save(new Player());
        var game = gameCreationService.createGame(new CreateMancalaGameParamsDto(4));
        game = joinGameService.joinGame(game.getId(), player.getId());

        assertThat(game.getPlayers().size()).isEqualTo(1);
        assertThat(game.getPlayers().get(0).getId()).isEqualTo(player.getId());
        assertThat(game.getStatus()).isEqualTo(MancalaGameStatus.NOT_STARTED);
        assertThat(game.isFull()).isFalse();
    }

    @Test
    void shouldJoinAGameWithOnePlayer() {
        var playerOne = playerRepository.save(new Player());
        var playerTwo = playerRepository.save(new Player());

        var game = gameCreationService.createGame(new CreateMancalaGameParamsDto(4));
        game = joinGameService.joinGame(game.getId(), playerOne.getId());
        game = joinGameService.joinGame(game.getId(), playerTwo.getId());

        assertThat(game.getPlayers().size()).isEqualTo(2);
        assertThat(game.isFull()).isTrue();
    }

    @Test
    void shouldDenyToJoinAFullGame() {
        var playerOne = playerRepository.save(new Player());
        var playerTwo = playerRepository.save(new Player());
        var playerThree = playerRepository.save(new Player());

        var game = gameCreationService.createGame(new CreateMancalaGameParamsDto(4));
        var gameId = game.getId();
        joinGameService.joinGame(gameId, playerOne.getId());
        joinGameService.joinGame(gameId, playerTwo.getId());

        assertThrows(
                GameIsFullException.class, () -> joinGameService.joinGame(gameId, playerThree.getId())
        );
    }

    @Test
    void shouldJoinAGameWithSamePlayerIdempotently() {
        var playerOne = playerRepository.save(new Player());

        var game = gameCreationService.createGame(new CreateMancalaGameParamsDto(4));
        game = joinGameService.joinGame(game.getId(), playerOne.getId());
        game = joinGameService.joinGame(game.getId(), playerOne.getId());

        assertThat(game.getPlayers().size()).isEqualTo(1);
        assertThat(game.getPlayers().get(0).getId()).isEqualTo(playerOne.getId());
        assertThat(game.getStatus()).isEqualTo(MancalaGameStatus.NOT_STARTED);
        assertThat(game.isFull()).isFalse();
    }

}

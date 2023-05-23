package com.mygame.mancala.integration.service;

import java.util.Objects;

import com.mygame.exception.EntityNotFoundException;
import com.mygame.exception.GameIllegalArgumentException;
import com.mygame.mancala.DTO.CreateMancalaGameParamsDto;
import com.mygame.mancala.integration.IntegrationTest;
import com.mygame.mancala.model.MancalaGame;
import com.mygame.mancala.model.pit.Pit;
import com.mygame.mancala.model.pit.PitType;
import com.mygame.mancala.repository.GameRepository;
import com.mygame.mancala.repository.PitRepository;
import com.mygame.mancala.service.MancalaGameCreationService;
import com.mygame.mancala.service.MancalaGamePlayService;
import com.mygame.mancala.service.MancalaJoinGameService;
import com.mygame.mancala.service.MancalaSowService;
import com.mygame.model.entity.Player;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.support.TransactionTemplate;
import repository.PlayerRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;

@RequiredArgsConstructor
public class MancalaSowServiceIntegrationTest extends IntegrationTest {

    private final MancalaSowService sowService;
    private final MancalaGameCreationService gameCreationService;
    private final PlayerRepository playerRepository;
    private final MancalaJoinGameService joinGameService;
    private final MancalaGamePlayService gamePlayService;
    private final TransactionTemplate transactionTemplate;
    private final GameRepository gameRepository;

    private final PitRepository pitRepository;

    @Test
    void shouldNotAllowToSowWithInvalidGameId() {
        var exception = assertThrows(
                EntityNotFoundException.class, () -> sowService.sow(-1L, -1L, -1L)
        );

        Assertions.assertThat(exception.getMessage()).isEqualTo("Entity not found: MancalaGame[-1]");
    }

    @Test
    void shouldNotAllowToSowWithGameInWrongStatus() {
        var game = gameCreationService.createGame(new CreateMancalaGameParamsDto(4));

        var exception = assertThrows(
                GameIllegalArgumentException.class, () -> sowService.sow(game.getId(), -1L, -1L)
        );

        Assertions.assertThat(exception.getMessage()).contains("has invalid status");
    }


    @Test
    void shouldNotAllowToSowWithWrongPlayerId() {
        var playerOne = playerRepository.save(new Player());
        var playerTwo = playerRepository.save(new Player());
        var game = gameCreationService.createGame(new CreateMancalaGameParamsDto(4));
        game = joinGameService.joinGame(game.getId(), playerOne.getId());
        game = joinGameService.joinGame(game.getId(), playerTwo.getId());
        game = gamePlayService.startIfNeeded(game.getId());
        var gameVarForLambda = game;
        var pitToStart = getWrongPitToStartWith(game);

        var exception = assertThrows(
                GameIllegalArgumentException.class,
                () -> sowService.sow(gameVarForLambda.getId(), pitToStart.getId(), -1L)
        );

        Assertions.assertThat(exception.getMessage()).isEqualTo("Its not your turn now!");
    }

    @NotNull
    private Pit getWrongPitToStartWith(MancalaGame game) {
        var gameId = game.getId();

        return Objects.requireNonNull(transactionTemplate.execute(ts -> {
            var gameVarForLambda = gameRepository.findByIdOrThrow(gameId);

            return gameVarForLambda.getBoard().getPits().stream()
                    .filter(pit -> !Objects.equals(pit.getPlayer().getId(), gameVarForLambda.getPlayerTurn().getId()))
                    .filter(pit -> pit.getType() != PitType.MANCALA)
                    .findAny().orElseThrow();
        }));
    }

    @Test
    void shouldNotAllowToSowOnOtherPlayerTurn() {
        var playerOne = playerRepository.save(new Player());
        var playerTwo = playerRepository.save(new Player());
        var game = gameCreationService.createGame(new CreateMancalaGameParamsDto(4));
        game = joinGameService.joinGame(game.getId(), playerOne.getId());
        game = joinGameService.joinGame(game.getId(), playerTwo.getId());
        game = gamePlayService.startIfNeeded(game.getId());

        var notActivePlayerId = Objects.equals(game.getPlayerTurn().getId(), playerOne.getId()) ?
                playerTwo.getId() : playerOne.getId();
        var gameVarForLambda = game;
        var pitToStart = getWrongPitToStartWith(game);

        var exception = assertThrows(
                GameIllegalArgumentException.class,
                () -> sowService.sow(gameVarForLambda.getId(), pitToStart.getId(), notActivePlayerId)
        );

        Assertions.assertThat(exception.getMessage()).isEqualTo("Its not your turn now!");
    }

    @Test
    void shouldNotAllowToSowFromOtherPlayerPit() {
        var playerOne = playerRepository.save(new Player());
        var playerTwo = playerRepository.save(new Player());
        var game = gameCreationService.createGame(new CreateMancalaGameParamsDto(4));
        game = joinGameService.joinGame(game.getId(), playerOne.getId());
        game = joinGameService.joinGame(game.getId(), playerTwo.getId());
        game = gamePlayService.startIfNeeded(game.getId());
        var gameVarForLambda = game;
        var pitToStart = getWrongPitToStartWith(game);

        var exception = assertThrows(
                GameIllegalArgumentException.class,
                () -> sowService.sow(gameVarForLambda.getId(), pitToStart.getId(),
                        gameVarForLambda.getPlayerTurn().getId())
        );

        Assertions.assertThat(exception.getMessage()).isEqualTo("Cannot sow from opponent's pit " + pitToStart.getId());
    }

    @Test
    void shouldNotAllowToSowFromEmptyPit() {
        var playerOne = playerRepository.save(new Player());
        var playerTwo = playerRepository.save(new Player());
        var game = gameCreationService.createGame(new CreateMancalaGameParamsDto(4));
        game = joinGameService.joinGame(game.getId(), playerOne.getId());
        game = joinGameService.joinGame(game.getId(), playerTwo.getId());
        game = gamePlayService.startIfNeeded(game.getId());
        var gameId = game.getId();

        var pitToStart = transactionTemplate.execute(ts -> {
            var gameVarForLambda = gameRepository.findByIdOrThrow(gameId);
            var pitToStartLambda = gameVarForLambda.getBoard().getPits().stream()
                    .filter(pit -> Objects.equals(pit.getPlayer().getId(), gameVarForLambda.getPlayerTurn().getId()))
                    .filter(pit -> pit.getType() != PitType.MANCALA)
                    .filter(pit -> pit.getNextPit().getType() != PitType.MANCALA)
                    .findFirst()
                    .orElseThrow();

            pitToStartLambda.clear();
            return pitRepository.save(pitToStartLambda);
        });

        var playerTurnId = game.getPlayerTurn().getId();
        var exception = assertThrows(
                GameIllegalArgumentException.class,
                () -> sowService.sow(gameId, pitToStart.getId(), playerTurnId)
        );

        Assertions.assertThat(exception.getMessage()).isEqualTo("Cannot sow from empty pit " + pitToStart.getId());
    }

    @Test
    void shouldNotAllowToSowFromMancalaPit() {
        var playerOne = playerRepository.save(new Player());
        var playerTwo = playerRepository.save(new Player());
        var game = gameCreationService.createGame(new CreateMancalaGameParamsDto(4));
        game = joinGameService.joinGame(game.getId(), playerOne.getId());
        game = joinGameService.joinGame(game.getId(), playerTwo.getId());
        game = gamePlayService.startIfNeeded(game.getId());
        var gameId = game.getId();

        var pitToStart = transactionTemplate.execute(ts -> {
            var gameVarForLambda = gameRepository.findByIdOrThrow(gameId);

            return gameVarForLambda.getBoard().getPits().stream()
                    .filter(pit -> Objects.equals(pit.getPlayer().getId(), gameVarForLambda.getPlayerTurn().getId()))
                    .filter(pit -> pit.getType() == PitType.MANCALA)
                    .findAny().orElseThrow();
        });

        var playerTurnId = game.getPlayerTurn().getId();
        var exception = assertThrows(
                GameIllegalArgumentException.class,
                () -> sowService.sow(gameId, pitToStart.getId(), playerTurnId)
        );

        Assertions.assertThat(exception.getMessage()).isEqualTo("Cannot sow from mancala!");
    }

    @Test
    void shouldSuccessfullySowAndSwitchActivePlayer() {
        var playerOne = playerRepository.save(new Player());
        var playerTwo = playerRepository.save(new Player());
        var game = gameCreationService.createGame(new CreateMancalaGameParamsDto(7));
        game = joinGameService.joinGame(game.getId(), playerOne.getId());
        game = joinGameService.joinGame(game.getId(), playerTwo.getId());
        game = gamePlayService.startIfNeeded(game.getId());
        var gameId = game.getId();

        var pitToStart = transactionTemplate.execute(ts -> {
            var gameVarForLambda = gameRepository.findByIdOrThrow(gameId);

            return gameVarForLambda.getBoard().getPits().stream()
                    .filter(pit -> Objects.equals(pit.getPlayer().getId(), gameVarForLambda.getPlayerTurn().getId()))
                    .filter(pit -> pit.getType() == PitType.NORMAL)
                    .findAny().orElseThrow();
        });

        var playerTurnId = game.getPlayerTurn().getId();
        var result = sowService.sow(gameId, pitToStart.getId(), playerTurnId);
        var resultPits = result.getBoard().getPits();

        Assertions.assertThat(
                        resultPits.stream()
                                .anyMatch(pit -> Objects.equals(pit.getId(), pitToStart.getId())))
                .isTrue();
        Assertions.assertThat(
                        resultPits.stream()
                                .anyMatch(pit -> pit.getType() == PitType.MANCALA && !pit.isEmpty()))
                .isTrue();
        Assertions.assertThat(result.getPlayerTurn().getId()).isNotEqualTo(game.getPlayerTurn().getId());
    }

    @Test
    void shouldGetAnotherTurn() {
        var playerOne = playerRepository.save(new Player());
        var playerTwo = playerRepository.save(new Player());
        var game = gameCreationService.createGame(new CreateMancalaGameParamsDto(4));
        game = joinGameService.joinGame(game.getId(), playerOne.getId());
        game = joinGameService.joinGame(game.getId(), playerTwo.getId());
        game = gamePlayService.startIfNeeded(game.getId());
        var gameId = game.getId();

        var pitToStart = transactionTemplate.execute(ts -> {
            var gameVarForLambda = gameRepository.findByIdOrThrow(gameId);

            var pitToStartLambda = gameVarForLambda.getBoard().getPits().stream()
                    .filter(pit -> Objects.equals(pit.getPlayer().getId(), gameVarForLambda.getPlayerTurn().getId()))
                    .filter(pit -> pit.getNextPit().getType() == PitType.MANCALA)
                    .findFirst()
                    .orElseThrow();

            pitToStartLambda.clear();
            pitToStartLambda.addStones(1);
            return pitRepository.save(pitToStartLambda);
        });

        var playerTurnId = game.getPlayerTurn().getId();
        var result = sowService.sow(gameId, pitToStart.getId(), playerTurnId);

        Assertions.assertThat(result.getPlayerTurn().getId()).isEqualTo(game.getPlayerTurn().getId());
    }

    @Test
    void shouldCaptureOppositeStonesAndAddToMancala() {
        var playerOne = playerRepository.save(new Player());
        var playerTwo = playerRepository.save(new Player());
        var game = gameCreationService.createGame(new CreateMancalaGameParamsDto(4));
        game = joinGameService.joinGame(game.getId(), playerOne.getId());
        game = joinGameService.joinGame(game.getId(), playerTwo.getId());
        game = gamePlayService.startIfNeeded(game.getId());
        var gameId = game.getId();

        var pitToStart = transactionTemplate.execute(ts -> {
            var gameVarForLambda = gameRepository.findByIdOrThrow(gameId);
            var pitToStartLambda = gameVarForLambda.getBoard().getPits().stream()
                    .filter(pit -> Objects.equals(pit.getPlayer().getId(), gameVarForLambda.getPlayerTurn().getId()))
                    .filter(pit -> pit.getType() != PitType.MANCALA)
                    .filter(pit -> pit.getNextPit().getType() != PitType.MANCALA)
                    .findFirst()
                    .orElseThrow();

            pitToStartLambda.clear();
            pitToStartLambda.addStones(1);

            var nextPit = pitToStartLambda.getNextPit();
            nextPit.clear();
            pitRepository.save(nextPit);
            return pitRepository.save(pitToStartLambda);

        });


        var result = sowService.sow(gameId, pitToStart.getId(), game.getPlayerTurn().getId());
        var playerTurnId = game.getPlayerTurn().getId();
        var resultMancala = result.getBoard().getPits().stream()
                .filter(pit -> pit.getType() == PitType.MANCALA)
                .filter(pit -> Objects.equals(pit.getPlayer().getId(), playerTurnId))
                .findFirst()
                .orElseThrow();

        Assertions.assertThat(resultMancala.getStones()).isEqualTo(5); // 4 from the other player pit + 1 from your pit
    }
}

package com.mygame.mancala.integration.service;

import java.util.Objects;

import com.mygame.exception.EntityNotFoundException;
import com.mygame.exception.GameIllegalArgumentException;
import com.mygame.mancala.DTO.CreateMancalaGameParamsDto;
import com.mygame.mancala.integration.IntegrationTest;
import com.mygame.mancala.model.MancalaGame;
import com.mygame.mancala.model.pit.Pit;
import com.mygame.mancala.model.pit.PitType;
import com.mygame.mancala.repository.PitRepository;
import com.mygame.mancala.service.MancalaGameCreationService;
import com.mygame.mancala.service.MancalaJoinGameService;
import com.mygame.mancala.service.MancalaSowService;
import com.mygame.model.entity.Player;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import repository.PlayerRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;

@RequiredArgsConstructor
public class MancalaSowServiceIntegrationTest extends IntegrationTest {

    private final MancalaSowService sowService;
    private final MancalaGameCreationService gameCreationService;
    private final PlayerRepository playerRepository;
    private final MancalaJoinGameService joinGameService;

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

        Assertions.assertThat(exception.getMessage()).isEqualTo("Game " + game.getId() + " has not started yet");
    }


    @Test
    void shouldNotAllowToSowWithWrongPlayerId() {
        var playerOne = playerRepository.save(new Player());
        var playerTwo = playerRepository.save(new Player());

        var game = gameCreationService.createGame(new CreateMancalaGameParamsDto(4));
        game = joinGameService.joinGame(game.getId(), playerOne.getId());
        game = joinGameService.joinGame(game.getId(), playerTwo.getId());

        var gameVarForLambda = game;
        var pitToStart = getWrongPitToStartWith(game, gameVarForLambda);

        var exception = assertThrows(
                GameIllegalArgumentException.class,
                () -> sowService.sow(gameVarForLambda.getId(), pitToStart.getId(), -1L)
        );

        Assertions.assertThat(exception.getMessage()).isEqualTo("Its not your turn now!");
    }

    @NotNull
    private static Pit getWrongPitToStartWith(MancalaGame game, MancalaGame gameVarForLambda) {
        return game.getBoard().getPits().stream()
                .filter(pit -> !Objects.equals(pit.getPlayer().getId(), gameVarForLambda.getPlayerTurn().getId()))
                .filter(pit -> pit.getType() != PitType.MANCALA)
                .findAny().orElseThrow();
    }

    @Test
    void shouldNotAllowToSowOnOtherPlayerTurn() {
        var playerOne = playerRepository.save(new Player());
        var playerTwo = playerRepository.save(new Player());

        var game = gameCreationService.createGame(new CreateMancalaGameParamsDto(4));
        game = joinGameService.joinGame(game.getId(), playerOne.getId());
        game = joinGameService.joinGame(game.getId(), playerTwo.getId());

        var notActivePlayerId = Objects.equals(game.getPlayerTurn().getId(), playerOne.getId()) ?
                playerTwo.getId() : playerOne.getId();

        var gameVarForLambda = game;

        var pitToStart = getWrongPitToStartWith(game, gameVarForLambda);;

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

        var gameVarForLambda = game;

        var pitToStart = getWrongPitToStartWith(game, gameVarForLambda);;

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

        var gameVarForLambda = game;

        var pitToStart = game.getBoard().getPits().stream()
                .filter(pit -> Objects.equals(pit.getPlayer().getId(), gameVarForLambda.getPlayerTurn().getId()))
                .filter(pit -> pit.getType() != PitType.MANCALA)
                .findAny().orElseThrow();

        pitToStart.clear();
        pitRepository.save(pitToStart);

        var exception = assertThrows(
                GameIllegalArgumentException.class,
                () -> sowService.sow(gameVarForLambda.getId(), pitToStart.getId(),
                        gameVarForLambda.getPlayerTurn().getId())
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

        var gameVarForLambda = game;

        var pitToStart = game.getBoard().getPits().stream()
                .filter(pit -> Objects.equals(pit.getPlayer().getId(), gameVarForLambda.getPlayerTurn().getId()))
                .filter(pit -> pit.getType() == PitType.MANCALA)
                .findAny().orElseThrow();

        var exception = assertThrows(
                GameIllegalArgumentException.class,
                () -> sowService.sow(gameVarForLambda.getId(), pitToStart.getId(),
                        gameVarForLambda.getPlayerTurn().getId())
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

        var gameVarForLambda = game;

        var pitToStart = game.getBoard().getPits().stream()
                .filter(pit -> Objects.equals(pit.getPlayer().getId(), gameVarForLambda.getPlayerTurn().getId()))
                .filter(pit -> pit.getType() == PitType.NORMAL)
                .findAny().orElseThrow();

        var result = sowService.sow(gameVarForLambda.getId(), pitToStart.getId(),
                gameVarForLambda.getPlayerTurn().getId());


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
        var gameVarForLambda = game;

        var pitToStart = game.getBoard().getPits().stream()
                .filter(pit -> Objects.equals(pit.getPlayer().getId(), gameVarForLambda.getPlayerTurn().getId()))
                .filter(pit -> pit.getNextPit().getType() == PitType.MANCALA)
                .findFirst()
                .orElseThrow();

        pitToStart.clear();
        pitToStart.addStones(1);
        pitRepository.save(pitToStart);

        var result = sowService.sow(gameVarForLambda.getId(), pitToStart.getId(),
                gameVarForLambda.getPlayerTurn().getId());


        Assertions.assertThat(result.getPlayerTurn().getId()).isEqualTo(game.getPlayerTurn().getId());
    }

    @Test
    void shouldCaptureOppositeStonesAndAddToMancala() {
        var playerOne = playerRepository.save(new Player());
        var playerTwo = playerRepository.save(new Player());

        var game = gameCreationService.createGame(new CreateMancalaGameParamsDto(4));
        game = joinGameService.joinGame(game.getId(), playerOne.getId());
        game = joinGameService.joinGame(game.getId(), playerTwo.getId());
        var gameVarForLambda = game;

        var pitToStart = game.getBoard().getPits().stream()
                .filter(pit -> Objects.equals(pit.getPlayer().getId(), gameVarForLambda.getPlayerTurn().getId()))
                .filter(pit -> pit.getType() != PitType.MANCALA)
                .filter(pit -> pit.getNextPit().getType() != PitType.MANCALA)
                .findFirst()
                .orElseThrow();

        pitToStart.clear();
        pitToStart.addStones(1);

        var nextPit = pitToStart.getNextPit();
        nextPit.clear();
        pitRepository.save(pitToStart);
        pitRepository.save(nextPit);

        var result = sowService.sow(gameVarForLambda.getId(), pitToStart.getId(),
                gameVarForLambda.getPlayerTurn().getId());

        var resultMancala = result.getBoard().getPits().stream()
                .filter(pit -> pit.getType() == PitType.MANCALA)
                .filter(pit -> Objects.equals(pit.getPlayer().getId(), gameVarForLambda.getPlayerTurn().getId()))
                .findFirst()
                .orElseThrow();

        Assertions.assertThat(resultMancala.getStones()).isEqualTo(5); // 4 from the other player pit + 1 from your pit
    }
}

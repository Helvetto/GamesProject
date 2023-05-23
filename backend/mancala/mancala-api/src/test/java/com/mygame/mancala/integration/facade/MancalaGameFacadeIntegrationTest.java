package com.mygame.mancala.integration.facade;

import java.util.Objects;

import com.mygame.exception.GameIllegalArgumentException;
import com.mygame.exception.GameIsFullException;
import com.mygame.mancala.DTO.CreateMancalaGameParamsDto;
import com.mygame.mancala.DTO.GameStatusDto;
import com.mygame.mancala.DTO.MancalaGameDto;
import com.mygame.mancala.DTO.PitTypeDto;
import com.mygame.mancala.DTO.PlayerDto;
import com.mygame.mancala.facade.MancalaGameFacade;
import com.mygame.mancala.integration.IntegrationTest;
import com.mygame.mancala.model.pit.Pit;
import com.mygame.mancala.model.pit.PitType;
import com.mygame.mancala.repository.GameRepository;
import com.mygame.mancala.repository.PitRepository;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.support.TransactionTemplate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RequiredArgsConstructor
public class MancalaGameFacadeIntegrationTest extends IntegrationTest {

    private final MancalaGameFacade facade;
    private final PitRepository pitRepository;
    private final TransactionTemplate transactionTemplate;
    private final GameRepository gameRepository;

    @Test
    void shouldThrowWhenTryingCreateAndJoinGame() {
        var exception = assertThrows(
                GameIllegalArgumentException.class, () -> facade.createAndJoinGame(new CreateMancalaGameParamsDto(3),
                        1L)
        );

        Assertions.assertThat(exception.getMessage()).contains("Number of stones must be at least");
    }

    @Test
    void shouldCreateAndJoinGame() {
        var result = facade.createAndJoinGame(new CreateMancalaGameParamsDto(4), null);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.id()).isNotNull();
        Assertions.assertThat(result.players()).isNotEmpty();
        Assertions.assertThat(result.info().status()).isEqualTo(GameStatusDto.NOT_STARTED);
        Assertions.assertThat(result.board()).isNotNull();
        assertThereIsNoWinnerAndIsNotDraw(result);
    }

    @Test
    void shouldJoinCreatedGameWithNewPlayerAndStartIt() {
        var game = facade.createAndJoinGame(new CreateMancalaGameParamsDto(4), null);
        var result = facade.joinGameAndStartIfNeeded(game.id(), null);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.id()).isEqualTo(game.id());
        Assertions.assertThat(result.players()).hasSize(2);
        Assertions.assertThat(result.info().status()).isEqualTo(GameStatusDto.IN_PROGRESS);
        assertThereIsNoWinnerAndIsNotDraw(result);
    }

    @Test
    void shouldDoNothingWhenTryingToJoinAGameWithAnExistingPlayer() {
        var game = facade.createAndJoinGame(new CreateMancalaGameParamsDto(4), null);
        var result = facade.joinGameAndStartIfNeeded(game.id(), game.players().get(0).id());

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.id()).isEqualTo(game.id());
        Assertions.assertThat(result.players()).hasSize(1);
        Assertions.assertThat(result.info().status()).isEqualTo(GameStatusDto.NOT_STARTED);
        assertThereIsNoWinnerAndIsNotDraw(result);
    }

    @Test
    void shouldNotAllowToJoinFullGame() {
        var game = facade.createAndJoinGame(new CreateMancalaGameParamsDto(4), null);
        facade.joinGameAndStartIfNeeded(game.id(), null);

        assertThrows(
                GameIsFullException.class, () -> facade.joinGameAndStartIfNeeded(game.id(), null)
        );
    }

    @Test
    void shouldSow() {
        var game = facade.createAndJoinGame(new CreateMancalaGameParamsDto(4), null);
        var gamyCopyForLambda = game;
        game = facade.joinGameAndStartIfNeeded(game.id(), null);

        var playerTurn = game.info().playerTurn();
        var pitToStartWith =
                game.board().pits().stream()
                        .filter(p -> Objects.equals(p.playerId(), playerTurn.id()))
                        .filter(p -> p.type() != PitTypeDto.MANCALA)
                        .findAny().orElseThrow();

        assertDoesNotThrow(() -> facade.sow(gamyCopyForLambda.id(), pitToStartWith.id(), playerTurn.id()));
    }

    @Test
    void shouldThrowOnWrongPlayerTurn() {
        var game = facade.createAndJoinGame(new CreateMancalaGameParamsDto(4), null);
        var gamyCopyForLambda = game;
        game = facade.joinGameAndStartIfNeeded(game.id(), null);

        var playerTurnId = game.info().playerTurn().id();
        var wrongPlayerTurn = game.players().stream().filter(p -> p.id() != playerTurnId).findFirst().orElseThrow();
        var pitToStartWith =
                game.board().pits().stream()
                        .filter(p -> Objects.equals(p.playerId(), wrongPlayerTurn.id()))
                        .filter(p -> p.type() != PitTypeDto.MANCALA)
                        .findAny().orElseThrow();

        var exception = assertThrows(
                GameIllegalArgumentException.class,
                () -> facade.sow(gamyCopyForLambda.id(), pitToStartWith.id(), wrongPlayerTurn.id())
        );

        Assertions.assertThat(exception.getMessage()).contains("Its not your turn now");
    }

    @Test
    void shouldThrowOnWrongPitToStartWith() {
        var game = facade.createAndJoinGame(new CreateMancalaGameParamsDto(4), null);
        var gamyCopyForLambda = game;
        game = facade.joinGameAndStartIfNeeded(game.id(), null);

        var playerTurn = game.info().playerTurn();
        var wrongPlayerTurn =
                game.players().stream().filter(p -> !p.id().equals(playerTurn.id())).findFirst().orElseThrow();
        var wrongPitToStartWith =
                game.board().pits().stream()
                        .filter(p -> Objects.equals(p.playerId(), wrongPlayerTurn.id()))
                        .filter(p -> p.type() != PitTypeDto.MANCALA)
                        .findAny().orElseThrow();

        var exception = assertThrows(
                GameIllegalArgumentException.class,
                () -> facade.sow(gamyCopyForLambda.id(), wrongPitToStartWith.id(), playerTurn.id())
        );

        Assertions.assertThat(exception.getMessage()).contains("Cannot sow from opponent's pit");
    }

    @Test
    void shouldThrowOnEmptyPitToStartWith() {
        var game = facade.createAndJoinGame(new CreateMancalaGameParamsDto(4), null);
        var gamyCopyForLambda = game;
        game = facade.joinGameAndStartIfNeeded(game.id(), null);

        var playerTurn = game.info().playerTurn();
        var pitToStartWith =
                game.board().pits().stream()
                        .filter(p -> Objects.equals(p.playerId(), playerTurn.id()))
                        .filter(p -> p.type() != PitTypeDto.MANCALA)
                        .findAny().orElseThrow();

        var pit = pitRepository.findByIdOrThrow(pitToStartWith.id());
        pit.clear();
        pitRepository.save(pit);

        var exception = assertThrows(
                GameIllegalArgumentException.class,
                () -> facade.sow(gamyCopyForLambda.id(), pitToStartWith.id(), playerTurn.id())
        );

        Assertions.assertThat(exception.getMessage()).contains("Cannot sow from empty pit");
    }

    @Test
    void shouldEndTheGameWithAWinner() {
        var gameDto = facade.createAndJoinGame(new CreateMancalaGameParamsDto(4), null);
        gameDto = facade.joinGameAndStartIfNeeded(gameDto.id(), null);
        var gameId = gameDto.id();

        var pitToStart = transactionTemplate.execute(ts -> {
            var gameVarForLambda = gameRepository.findByIdOrThrow(gameId);

            gameVarForLambda.getBoard().getPits().forEach(Pit::clear);
            var pitToStartLambda = gameVarForLambda.getBoard().getPits().stream()
                    .filter(pit -> Objects.equals(pit.getPlayer().getId(), gameVarForLambda.getPlayerTurn().getId()))
                    .filter(pit -> pit.getNextPit().getType() == PitType.MANCALA)
                    .findFirst()
                    .orElseThrow();

            pitToStartLambda.addStones(1);
            pitRepository.saveAll(gameVarForLambda.getBoard().getPits());
            return pitToStartLambda;
        });

        var playerTurnId =gameDto.info().playerTurn().id();
        var result = facade.sow(gameId, pitToStart.getId(), playerTurnId);

        Assertions.assertThat(result.info().status()).isEqualTo(GameStatusDto.FINISHED);
        Assertions.assertThat(result.info().draw()).isFalse();
        Assertions.assertThat(result.info().winner()).isNotNull();
    }

    @Test
    void shouldEndTheGameWithoutAWinnerAndWithDraw() {
        var gameDto = facade.createAndJoinGame(new CreateMancalaGameParamsDto(4), null);
        gameDto = facade.joinGameAndStartIfNeeded(gameDto.id(), null);
        var gameId = gameDto.id();

        var pitToStart = transactionTemplate.execute(ts -> {
            var gameVarForLambda = gameRepository.findByIdOrThrow(gameId);

            gameVarForLambda.getBoard().getPits().forEach(Pit::clear);
            var pitToStartLambda = gameVarForLambda.getBoard().getPits().stream()
                    .filter(pit -> Objects.equals(pit.getPlayer().getId(), gameVarForLambda.getPlayerTurn().getId()))
                    .filter(pit -> pit.getNextPit().getType() == PitType.MANCALA)
                    .findFirst()
                    .orElseThrow();

            pitToStartLambda.addStones(1);
            var oppositePlayerMancala = gameVarForLambda.getBoard().getPits().stream()
                    .filter(pit -> pit.getType() == PitType.MANCALA)
                    .filter(pit -> !Objects.equals(pit.getPlayer().getId(), pitToStartLambda.getPlayer().getId()))
                    .findFirst()
                    .orElseThrow();
            oppositePlayerMancala.addStones(1);

            pitRepository.saveAll(gameVarForLambda.getBoard().getPits());

            return pitToStartLambda;
        });

        var playerTurnId = gameDto.info().playerTurn().id();
        var result = facade.sow(gameId, pitToStart.getId(), playerTurnId);

        Assertions.assertThat(result.info().status()).isEqualTo(GameStatusDto.FINISHED);
        Assertions.assertThat(result.info().draw()).isTrue();
        Assertions.assertThat(result.info().winner()).isNull();
    }

    private static void assertThereIsNoWinnerAndIsNotDraw(MancalaGameDto result) {
        Assertions.assertThat(result.info().winner()).isNull();
        Assertions.assertThat(result.info().draw()).isFalse();
    }
}

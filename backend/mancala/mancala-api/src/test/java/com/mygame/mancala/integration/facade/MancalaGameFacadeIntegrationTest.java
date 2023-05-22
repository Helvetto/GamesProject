package com.mygame.mancala.integration.facade;

import java.util.Objects;

import com.mygame.exception.GameIllegalArgumentException;
import com.mygame.exception.GameIsFullException;
import com.mygame.mancala.DTO.CreateMancalaGameParamsDto;
import com.mygame.mancala.DTO.GameStatusDto;
import com.mygame.mancala.DTO.PitTypeDto;
import com.mygame.mancala.DTO.PlayerDto;
import com.mygame.mancala.facade.MancalaGameFacade;
import com.mygame.mancala.integration.IntegrationTest;
import com.mygame.mancala.repository.PitRepository;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RequiredArgsConstructor
public class MancalaGameFacadeIntegrationTest extends IntegrationTest {

    private final MancalaGameFacade facade;
    private final PitRepository pitRepository;

    @Test
    void shouldThrowWhenTryingCreateAndJoinGame() {
        var exception = assertThrows(
                GameIllegalArgumentException.class, () -> facade.createAndJoinGame(new CreateMancalaGameParamsDto(3), 1L)
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
    }

    @Test
    void shouldJoinCreatedGameWithNewPlayerAndStartIt() {
        var game = facade.createAndJoinGame(new CreateMancalaGameParamsDto(4), null);

        var result = facade.joinGame(game.id(), null);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.id()).isEqualTo(game.id());
        Assertions.assertThat(result.players()).hasSize(2);
        Assertions.assertThat(result.info().status()).isEqualTo(GameStatusDto.IN_PROGRESS);
    }

    @Test
    void shouldDoNothingWhenTryingToJoinAGameWithAnExistingPlayer() {
        var game = facade.createAndJoinGame(new CreateMancalaGameParamsDto(4), null);

        var result = facade.joinGame(game.id(), game.players().get(0).id());

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.id()).isEqualTo(game.id());
        Assertions.assertThat(result.players()).hasSize(1);
        Assertions.assertThat(result.info().status()).isEqualTo(GameStatusDto.NOT_STARTED);
    }

    @Test
    void shouldNotAllowToJoinFullGame() {
        var game = facade.createAndJoinGame(new CreateMancalaGameParamsDto(4), null);
        facade.joinGame(game.id(), null);

        assertThrows(
                GameIsFullException.class, () -> facade.joinGame(game.id(), null)
        );
    }

    @Test
    void shouldSow() {
        var game = facade.createAndJoinGame(new CreateMancalaGameParamsDto(4), null);
        var gamyCopyForLambda = game;
        game = facade.joinGame(game.id(), null);

        var playerTurn = game.players().stream().filter(PlayerDto::isHisTurn).findFirst().orElseThrow();
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
        game = facade.joinGame(game.id(), null);

        var wrongPlayerTurn = game.players().stream().filter(p -> !p.isHisTurn()).findFirst().orElseThrow();
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
        game = facade.joinGame(game.id(), null);

        var wrongPlayerTurn = game.players().stream().filter(p -> !p.isHisTurn()).findFirst().orElseThrow();
        var playerTurn = game.players().stream().filter(PlayerDto::isHisTurn).findFirst().orElseThrow();
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
        game = facade.joinGame(game.id(), null);

        var playerTurn = game.players().stream().filter(PlayerDto::isHisTurn).findFirst().orElseThrow();
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
}

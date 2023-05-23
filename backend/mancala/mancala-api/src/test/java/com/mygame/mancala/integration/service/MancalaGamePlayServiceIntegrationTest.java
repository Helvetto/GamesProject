package com.mygame.mancala.integration.service;

import java.util.Objects;

import com.mygame.mancala.DTO.CreateMancalaGameParamsDto;
import com.mygame.mancala.integration.IntegrationTest;
import com.mygame.mancala.model.MancalaGameStatus;
import com.mygame.mancala.model.pit.Pit;
import com.mygame.mancala.model.pit.PitType;
import com.mygame.mancala.repository.GameRepository;
import com.mygame.mancala.repository.PitRepository;
import com.mygame.mancala.service.MancalaGameCreationService;
import com.mygame.mancala.service.MancalaGamePlayService;
import com.mygame.mancala.service.MancalaJoinGameService;
import com.mygame.model.entity.Player;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.support.TransactionTemplate;
import repository.PlayerRepository;

@RequiredArgsConstructor
public class MancalaGamePlayServiceIntegrationTest extends IntegrationTest {

    private final MancalaGamePlayService gamePlayService;
    private final MancalaGameCreationService gameCreationService;
    private final MancalaJoinGameService joinGameService;
    private final PlayerRepository playerRepository;
    private final PitRepository pitRepository;
    private final GameRepository gameRepository;
    private final TransactionTemplate transactionTemplate;

    @Test
    void shouldFinishGame() {
        var playerOne = playerRepository.save(new Player());
        var playerTwo = playerRepository.save(new Player());
        var game = gameCreationService.createGame(new CreateMancalaGameParamsDto(4));
        game = joinGameService.joinGame(game.getId(), playerOne.getId());
        game = joinGameService.joinGame(game.getId(), playerTwo.getId());
        game = gamePlayService.startIfNeeded(game.getId());
        var gameId = game.getId();

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


        var result = gamePlayService.sow(game.getId(), pitToStart.getId(), game.getPlayerTurn().getId());
        Assertions.assertThat(result.getBoard().getPits().stream().filter(pit -> pit.getType() != PitType.MANCALA).allMatch(Pit::isEmpty)).isTrue();
        Assertions.assertThat(result.getStatus()).isEqualTo(MancalaGameStatus.FINISHED);
    }
}

package com.mygame.mancala.integration.service;

import java.util.Objects;

import com.mygame.mancala.DTO.CreateMancalaGameParamsDto;
import com.mygame.mancala.integration.IntegrationTest;
import com.mygame.mancala.model.MancalaGameStatus;
import com.mygame.mancala.model.pit.Pit;
import com.mygame.mancala.model.pit.PitType;
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


    @Test
    void shouldFinishGame() {
        var playerOne = playerRepository.save(new Player());
        var playerTwo = playerRepository.save(new Player());
        var game = gameCreationService.createGame(new CreateMancalaGameParamsDto(4));
        game = joinGameService.joinGame(game.getId(), playerOne.getId());
        game = joinGameService.joinGame(game.getId(), playerTwo.getId());
        var gameVarForLambda = game;
        game.getBoard().getPits().forEach(Pit::clear);
        var pitToStart = game.getBoard().getPits().stream()
                .filter(pit -> Objects.equals(pit.getPlayer().getId(), gameVarForLambda.getPlayerTurn().getId()))
                .filter(pit -> pit.getNextPit().getType() == PitType.MANCALA)
                .findFirst()
                .orElseThrow();

        pitToStart.addStones(1);
        pitRepository.saveAll(game.getBoard().getPits());

        var result = gamePlayService.sow(game.getId(), pitToStart.getId(), game.getPlayerTurn().getId());
        Assertions.assertThat(result.getBoard().getPits().stream().filter(pit -> pit.getType() != PitType.MANCALA).allMatch(Pit::isEmpty)).isTrue();
        Assertions.assertThat(result.getStatus()).isEqualTo(MancalaGameStatus.FINISHED);
    }
}

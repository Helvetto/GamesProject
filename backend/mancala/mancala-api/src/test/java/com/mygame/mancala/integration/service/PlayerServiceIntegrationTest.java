package com.mygame.mancala.integration.service;

import com.mygame.common.service.PlayerService;
import com.mygame.mancala.DTO.CreateMancalaGameParamsDto;
import com.mygame.mancala.facade.MancalaGameFacade;
import com.mygame.mancala.integration.IntegrationTest;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.support.TransactionTemplate;
import repository.PlayerRepository;

@RequiredArgsConstructor
public class PlayerServiceIntegrationTest extends IntegrationTest {

    private final PlayerService playerService;
    private final MancalaGameFacade facade;
    private final PlayerRepository playerRepository;
    private final TransactionTemplate transactionTemplate;

    @Test
    void shouldRemoveGameFromActive() {
        var game = facade.createAndJoinGame(new CreateMancalaGameParamsDto(4), null);
        var playerBeforeRemove = transactionTemplate.execute(ts -> {
            var player = playerRepository.findByIdOrThrow(game.players().get(0).id());
            Assertions.assertThat(player.getActiveGames()).hasSize(1);
            return player;
        });

        playerService.remove(playerBeforeRemove.getId(), game.id());

        transactionTemplate.executeWithoutResult(ts -> {
            var playerAfterRemove = playerRepository.findByIdOrThrow(playerBeforeRemove.getId());
            Assertions.assertThat(playerAfterRemove.getActiveGames()).isEmpty();
        });

    }

    @Test
    void shouldRemoveGameFromActiveIdempotently() {
        var game = facade.createAndJoinGame(new CreateMancalaGameParamsDto(4), null);
        var playerBeforeRemove = transactionTemplate.execute(ts -> {
            var player = playerRepository.findByIdOrThrow(game.players().get(0).id());
            Assertions.assertThat(player.getActiveGames()).hasSize(1);
            return player;
        });

        playerService.remove(playerBeforeRemove.getId(), game.id());
        playerService.remove(playerBeforeRemove.getId(), game.id());

        transactionTemplate.executeWithoutResult(ts -> {
            var playerAfterRemove = playerRepository.findByIdOrThrow(playerBeforeRemove.getId());
            Assertions.assertThat(playerAfterRemove.getActiveGames()).isEmpty();
        });
    }

}

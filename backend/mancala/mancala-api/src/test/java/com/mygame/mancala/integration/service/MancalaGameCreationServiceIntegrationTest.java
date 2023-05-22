package com.mygame.mancala.integration.service;

import com.mygame.mancala.DTO.CreateMancalaGameParamsDto;
import com.mygame.mancala.integration.IntegrationTest;
import com.mygame.mancala.service.MancalaGameCreationService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RequiredArgsConstructor
public class MancalaGameCreationServiceIntegrationTest extends IntegrationTest {

    private final MancalaGameCreationService mancalaGameCreationService;

    @Test
    void shoudlCreateGame() {
        var dto = new CreateMancalaGameParamsDto(4);
        var game = mancalaGameCreationService.createGame(dto);

        assertNotNull(game);
        assertNotNull(game.getBoard());
        assertNotNull(game.getBoard().getId());
        assertNotNull(game.getBoard().getPits());
        assertNotNull(game.getPlayers());
        assertEquals(0, game.getPlayers().size());
        assertTrue(game.getBoard().getPits().stream().allMatch(pit -> pit.getNextPit() != null));

    }
}

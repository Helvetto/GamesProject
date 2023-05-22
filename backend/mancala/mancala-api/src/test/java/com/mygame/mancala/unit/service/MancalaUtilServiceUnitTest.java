package com.mygame.mancala.unit.service;

import java.util.List;

import com.mygame.mancala.unit.MockitoUnitTest;
import com.mygame.mancala.service.MancalaUtilService;
import com.mygame.model.entity.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class MancalaUtilServiceUnitTest extends MockitoUnitTest {

    @Test
    void testFindNewPlayersAmongAll() {
        var player1 = mock(Player.class);
        var player2 = mock(Player.class);
        var player3 = mock(Player.class);
        var player4 = mock(Player.class);

        var existingPlayers = List.of(player1, player2);
        var allPlayers = List.of(player2, player3, player4);

        var newPlayers = MancalaUtilService.findNewPlayersAmongAll(existingPlayers, allPlayers);

        assertEquals(2, newPlayers.size());
        assertTrue(newPlayers.contains(player3));
        assertTrue(newPlayers.contains(player4));
    }
}

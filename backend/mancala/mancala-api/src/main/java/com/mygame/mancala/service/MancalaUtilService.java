package com.mygame.mancala.service;

import java.util.List;

import com.mygame.model.entity.Player;

public class MancalaUtilService {

    /**
     * Finds the players in the list of all players that are not already in the list of existing players.
     *
     * @param existingPlayers the list of players that already exist
     * @param allPlayers      the list of all players to search for new players
     * @return the list of new players found among all players
     */
    public static List<Player> findNewPlayersAmongAll(List<Player> existingPlayers, List<Player> allPlayers) {
        return allPlayers.stream()
                .filter(p -> !existingPlayers.contains(p))
                .toList();
    }
}

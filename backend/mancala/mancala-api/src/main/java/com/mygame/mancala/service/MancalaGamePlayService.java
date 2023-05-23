package com.mygame.mancala.service;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import com.mygame.mancala.model.MancalaGame;
import com.mygame.mancala.model.MancalaGameStatus;
import com.mygame.mancala.model.pit.Pit;
import com.mygame.mancala.model.pit.PitType;
import com.mygame.mancala.repository.GameRepository;
import com.mygame.model.entity.Player;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MancalaGamePlayService {

    private final MancalaSowService sowService;
    private final GameRepository gameRepository;


    @Transactional
    public MancalaGame sow(Long gameId, Long pitId, Long playerId) {
        var game = gameRepository.findByIdOrThrow(gameId);
        game = sowService.sow(gameId, pitId, playerId);
        return finishGameIfNeeded(game);
    }

    @Transactional
    public MancalaGame startIfNeeded(Long gameId) {
        var game = gameRepository.findByIdOrThrow(gameId);
        if (game.isFull()) {
            startGame(game);
            return gameRepository.save(game);
        }
        return game;
    }


    /**
     * Finishes the game if needed based on the state of the Mancala game.
     * If any player's side is empty, all remaining stones are distributed to their respective Mancala pit,
     * and the game is marked as finished.
     *
     * @param game The Mancala game to check and potentially finish.
     * @return The updated Mancala game, possibly with a new status if the game has finished.
     */
    private MancalaGame finishGameIfNeeded(MancalaGame game) {
        var playerToPits = game.getBoard().getPits().stream()
                .collect(Collectors.groupingBy(Pit::getPlayer));

        if (shouldFinishGame(playerToPits)) {
            distributeStonesToMancalaPit(playerToPits);
            game.setStatus(MancalaGameStatus.FINISHED);
            return gameRepository.save(game);
        }

        return game;
    }

    /**
     * Checks if any player's side is empty, indicating that the game should be finished.
     *
     * @param playerToPits A mapping of players to their respective pits.
     * @return {@code true} if any player's side is empty, {@code false} otherwise.
     */
    private boolean shouldFinishGame(Map<Player, List<Pit>> playerToPits) {
        for (List<Pit> pits : playerToPits.values()) {
            if (isPlayerSideEmpty(pits)) {
                return true;
            }
        }
        return false;
    }

    private void distributeStonesToMancalaPit(Map<Player, List<Pit>> playerToPits) {
        for (List<Pit> pits : playerToPits.values()) {
            if (isPlayerSideEmpty(pits)) {
                int totalStones = calculateTotalStones(pits);
                pits.forEach(Pit::clear);
                distributeStonesToMancalaPit(pits, totalStones);
            }
        }
    }

    private boolean isPlayerSideEmpty(List<Pit> pits) {
        for (Pit pit : pits) {
            if (pit.getType() == PitType.NORMAL && !pit.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private int calculateTotalStones(List<Pit> pits) {
        int totalStones = 0;
        for (Pit pit : pits) {
            totalStones += pit.getStones();
        }
        return totalStones;
    }

    private void distributeStonesToMancalaPit(List<Pit> pits, int totalStones) {
        for (Pit pit : pits) {
            if (pit.getType() == PitType.MANCALA) {
                pit.addStones(totalStones);
            }
        }
    }


    /**
     * Starts the mancala game by randomly selecting a player to take the first turn,
     * setting the game status to "IN_PROGRESS"
     *
     * @param game the mancala game to be started
     * @implNote Uses the {@link Random} class to randomly select a player from the list of players in the game.
     */
    private void startGame(MancalaGame game) {
        var random = new Random();
        var players = game.getPlayers();
        var playerTurn = players.get(random.nextInt(players.size()));
        game.setPlayerTurn(playerTurn);
        game.setStatus(MancalaGameStatus.IN_PROGRESS);
    }

}

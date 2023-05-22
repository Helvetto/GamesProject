package com.mygame.mancala.service;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import com.mygame.common.util.validation.JoinGameValidations;
import com.mygame.exception.GameIllegalArgumentException;
import com.mygame.mancala.model.MancalaGame;
import com.mygame.mancala.model.MancalaGameStatus;
import com.mygame.mancala.model.pit.Pit;
import com.mygame.mancala.model.pit.PitType;
import com.mygame.mancala.repository.GameRepository;
import com.mygame.mancala.repository.PitRepository;
import com.mygame.model.entity.Game;
import com.mygame.model.entity.Player;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.PlayerRepository;

@Service
@RequiredArgsConstructor
public class MancalaJoinGameService {

    private final PlayerRepository playerRepository;
    private final PitRepository pitRepository;
    private final GameRepository gameRepository;

    @Transactional
    public MancalaGame joinGame(Long gameId, @Nullable Long playerId) {
        var game = gameRepository.findByIdOrThrow(gameId);

        if (game.getPlayers().stream()
                .anyMatch(p -> Objects.equals(p.getId(), playerId))) {
            return game;
        }

        JoinGameValidations.validateGame(game);

        var player = preparePlayer(game, playerId);
        assignPitsToPlayer(game.getBoard().getPits(), player);
        addPlayerToTheGame(player, game);

        if (game.isFull()) {
            startGame(game);
        }
        return gameRepository.save(game);
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

    /**
     * Creates a new player object and saves it in the database with the provided gameId added to their active game ids.
     *
     * @param gameId the id of the game the player will be assigned to
     * @return the newly created player object
     */
    private Player preparePlayer(Game gameId, Long playerId) {
        var player = playerId == null ? new Player() : playerRepository.findByIdOrThrow(playerId);
        player.addActiveGameIds(Set.of(gameId));
        return playerRepository.save(player);
    }

    private MancalaGame addPlayerToTheGame(Player playerTwo, MancalaGame game) {
        game.addPlayer(playerTwo);
        return game;
    }

    /**
     * Assigns all pits between an empty Mancala pit and the next Mancala pit to the specified player.
     * This method is used during the initialization of the game board.
     * <p>
     *
     * @param pits   the list of pits to be assigned to the player
     * @param player the player who will own the assigned pits
     * @throws GameIllegalArgumentException if all pits have already been assigned to players
     * @implNote Traverses the loop until it reaches the next Mancala pit in a
     * counter-clockwise direction
     * because the links between the pits are organized in a counter-clockwise
     * direction,
     * and according to the Mancala game rules, the player's Mancala pit is
     * located to their right.
     */
    private void assignPitsToPlayer(List<Pit> pits, Player player) {
        // Find an empty Mancala pit that has not yet been assigned to a player
        var emptyMancalaPit = pits.stream()
                .filter(pit -> pit.getType() == PitType.MANCALA && pit.getPlayer() == null)
                .findFirst()
                .orElseThrow(() -> new GameIllegalArgumentException("All pits are already assigned to players"));

        // Traverse the loop until we reach the next Mancala pit in a counter-clockwise direction
        var currentPit = emptyMancalaPit.getNextPit();
        while (currentPit.getType() != PitType.MANCALA) {
            currentPit = currentPit.getNextPit();
        }

        // Assign all pits between the starting pit and the next Mancala pit to the player
        currentPit = currentPit.getNextPit();
        while (!Objects.equals(currentPit, emptyMancalaPit)) {
            currentPit.setPlayer(player);
            currentPit = currentPit.getNextPit();
        }
        emptyMancalaPit.setPlayer(player);
        pitRepository.saveAll(pits);
    }
}

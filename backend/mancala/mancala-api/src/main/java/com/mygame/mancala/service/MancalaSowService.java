package com.mygame.mancala.service;

import java.util.List;
import java.util.Objects;

import com.mygame.exception.EntityNotFoundException;
import com.mygame.exception.GameIllegalArgumentException;
import com.mygame.mancala.model.Board;
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
public class MancalaSowService {
    private final GameRepository gameRepository;

    @Transactional
    public MancalaGame sow(Long gameId, Long pitId, Long playerId) {
        var game = gameRepository.findByIdOrThrow(gameId);
        validate(pitId, playerId, game);

        var pit = getPit(game, pitId);
        var lastPit = sow(pit, game);
        switchPlayerTurnIfNeeded(game, lastPit);

        return gameRepository.save(game);
    }

    private void validate(Long pitId, Long playerId, MancalaGame game) {
        validateGameStatus(game);
        validateNotMancalaPit(game, pitId);
        validateTurn(playerId, game);
        validatePit(game, pitId);
    }

    private void validateNotMancalaPit(MancalaGame game, Long pitId) {
        var pit = getPit(game, pitId);
        if (pit.getType() == PitType.MANCALA) {
            throw new GameIllegalArgumentException("Cannot sow from mancala!");
        }
    }

    private static void validateTurn(Long playerId, MancalaGame game) {
        if (!Objects.equals(game.getPlayerTurn().getId(), playerId)) {
            throw new GameIllegalArgumentException("Its not your turn now!");
        }
    }

    private void validateGameStatus(MancalaGame game) {
        if (game.getStatus() != MancalaGameStatus.IN_PROGRESS) {
            throw new GameIllegalArgumentException(
                    "Game {} has invalid status {}, should be {}",
                    game.getId(),
                    game.getStatus(),
                    MancalaGameStatus.IN_PROGRESS
            );
        }
    }

    private void validatePit(MancalaGame game, Long pitId) {
        var pit = getPit(game, pitId);
        validatePitOwnership(game, pitId, pit);
        validatePitIsNotEmpty(pitId, pit);
    }

    private static void validatePitIsNotEmpty(Long pitId, Pit pit) {
        if (pit.isEmpty()) {
            throw new GameIllegalArgumentException("Cannot sow from empty pit {}", pitId);
        }
    }

    private static void validatePitOwnership(MancalaGame game, Long pitId, Pit pit) {
        if (!Objects.equals(pit.getPlayer().getId(), game.getPlayerTurn().getId())) {
            throw new GameIllegalArgumentException("Cannot sow from opponent's pit {}", pitId);
        }
    }

    /**
     * Sows stones from the selected pit according to the rules of the Mancala game.
     * The stones are sown clockwise into each pit including the player's Mancala, but not the opponent's Mancala.
     * If the last stone lands in an empty pit, and the pit is owned by the current player, the player captures all
     * the stones in the opposite pit and adds them to their own Mancala.
     *
     * @param pit  The pit from which to start sowing stones.
     * @param game The Mancala game being played.
     * @return the last pit where the current player placed a stone during their turn.
     */
    private Pit sow(Pit pit, MancalaGame game) {
        int stonesToSow = pit.getStones();
        pit.clear();

        while (stonesToSow > 0) {
            pit = pit.getNextPit();

            if (!canSowInPit(pit, game.getPlayerTurn())) {
                continue;
            }

            pit.sow();
            stonesToSow--;

            if (shouldCaptureStones(pit, game.getPlayerTurn(), stonesToSow)) {
                captureStones(pit, findOppositePit(pit), game.getBoard());
            }
        }
        return pit;
    }

    /**
     * Finds the opposite pit of a given pit on the board.
     * The opposite pit is defined as the pit on the opposite side of the board,
     * i.e., the pit that is the same distance from the opponent's mancala pit as the given pit is
     * from the player's mancala pit.
     *
     * @param currentPit the pit for which to find the opposite pit
     * @return the opposite pit of the given pit
     */
    private Pit findOppositePit(Pit currentPit) {
        int distanceToMancala = 0;
        Pit pit = currentPit;

        while (pit.getType() != PitType.MANCALA) {
            distanceToMancala++;
            pit = pit.getNextPit();
        }
        for (int i = 0; i < distanceToMancala; i++) {
            pit = pit.getNextPit();
        }
        return pit;
    }

    private Pit getPit(MancalaGame game, Long pitId) {
        return game.getBoard().getPits().stream()
                .filter(p -> p.getId().equals(pitId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(Pit.class.getSimpleName(), pitId));
    }

    /**
     * Determines whether a player is allowed to sow seeds in a specific pit, based on the rules of Mancala.
     * A player can only sow seeds in a pit if:
     * The pit is a normal pit (i.e., not a Mancala pit)
     * The pit belongs to the current player
     * If the pit is a Mancala pit, the player can only sow seeds if it is their own Mancala pit.
     *
     * @param pit    the pit to check if sowing is allowed
     * @param player the current player
     * @return true if the player is allowed to sow seeds in the pit, false otherwise
     */
    private boolean canSowInPit(Pit pit, Player player) {
        return pit.getType() != PitType.MANCALA || pit.getPlayer() == player;
    }

    /**
     * Determines whether the pit should be captured, based on the rules of the game. A pit should be captured if it
     * is a
     * normal pit, belongs to the current player, and contains exactly one stone after the current player's turn
     * (since they add the last stone to the pit).
     *
     * @param pit    the pit to check for capture
     * @param player the current player
     * @return true if the pit should be captured, false otherwise
     */
    private boolean shouldCaptureStones(Pit pit, Player player, int stonesLeft) {
        return stonesLeft == 0 & pit.getPlayer() == player && pit.getType() == PitType.NORMAL && pit.getStones() == 1;
    }

    /**
     * Captures the stones from the player's pit and the opposite pit and adds them to the player's mancala.
     *
     * @param pit         The player's pit where the last stone was sowed
     * @param oppositePit The opposite pit of the player's pit
     * @param board       The game board
     */
    private void captureStones(Pit pit, Pit oppositePit, Board board) {
        int oppositePitStones = oppositePit.getStones();
        int stonesFromYourPit = pit.getStones();
        oppositePit.clear();
        pit.clear();
        var mancala = getMancalaPit(pit.getPlayer(), board.getPits());
        mancala.addStones(oppositePitStones + stonesFromYourPit);
    }

    private Pit getMancalaPit(Player player, List<Pit> pits) {
        return pits.stream()
                .filter(p -> p.getType() == PitType.MANCALA && p.getPlayer() == player)
                .findFirst()
                .orElseThrow();
    }

    /**
     * Switches the turn to the other player based on the last pit that was played.
     * If the last pit was a player's mancala, the player gets another turn. Otherwise, it switches to the other
     * player's turn.
     *
     * @param game    the current game of Mancala being played
     * @param lastPit the last pit that was played
     */
    private void switchPlayerTurnIfNeeded(MancalaGame game, Pit lastPit) {
        var currentPlayer = game.getPlayerTurn();
        if (lastPit.getPlayer() == currentPlayer && lastPit.getType() == PitType.MANCALA) {
            return;
        }
        var otherPlayer = getOtherPlayer(game, currentPlayer);
        game.setPlayerTurn(otherPlayer);
    }

    private Player getOtherPlayer(MancalaGame game, Player currentPlayer) {
        return game.getPlayers().stream()
                .filter(p -> !p.equals(currentPlayer))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(Player.class.getSimpleName()));
    }


}

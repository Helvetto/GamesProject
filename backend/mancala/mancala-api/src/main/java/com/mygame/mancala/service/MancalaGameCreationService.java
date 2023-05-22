package com.mygame.mancala.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.mygame.mancala.DTO.CreateMancalaGameParamsDto;
import com.mygame.mancala.model.Board;
import com.mygame.mancala.model.MancalaGame;
import com.mygame.mancala.model.pit.Pit;
import com.mygame.mancala.model.pit.PitType;
import com.mygame.mancala.repository.BoardRepository;
import com.mygame.mancala.repository.GameRepository;
import com.mygame.mancala.repository.PitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MancalaGameCreationService {
    private static final int BOARD_SIZE = 6;
    private final BoardRepository boardRepository;
    private final GameRepository gameRepository;

    @Transactional
    public MancalaGame createGame(CreateMancalaGameParamsDto dto) {
        int numberOfHouseStones = dto.numberOfStones();
        var board = prepareBoard(numberOfHouseStones);
        return prepareGame(board);
    }


    /**
     * Prepares the game board by creating pits for both players and linking them together.
     *
     * @param numberOfHouseStones the number of stones to initially place in each house pit
     * @return the prepared board object
     */
    private Board prepareBoard(int numberOfHouseStones) {
        List<Pit> pits = new ArrayList<>();

        var playerOnePits = preparePits(BOARD_SIZE, numberOfHouseStones, PitType.NORMAL);
        pits.addAll(playerOnePits);

        var playerOneStore = preparePits(1, 0, PitType.MANCALA).get(0);
        pits.add(playerOneStore);

        var playerTwoPits = preparePits(BOARD_SIZE, numberOfHouseStones, PitType.NORMAL);
        pits.addAll(playerTwoPits);

        var playerTwoStore = preparePits(1, 0, PitType.MANCALA).get(0);
        pits.add(playerTwoStore);

        linkPits(playerOnePits, playerOneStore);
        playerOneStore.setNextPit(playerTwoPits.get(0));
        linkPits(playerTwoPits, playerTwoStore);
        playerTwoStore.setNextPit(playerOnePits.get(0));

        return boardRepository.save(new Board(pits));
    }

    /**
     * Creates the specified number of pits of the specified type, each initially containing the specified number of
     * stones.
     *
     * @param numberOfPits   the number of pits to create
     * @param numberOfStones the number of stones to initially place in each pit
     * @param type           the type of pit to create (either NORMAL or MANCALA)
     * @return a list of the created pits
     */
    private List<Pit> preparePits(int numberOfPits, int numberOfStones, PitType type) {
        return IntStream.range(0, numberOfPits)
                        .mapToObj(i -> new Pit(numberOfStones, type))
                        .toList();
    }

    /**
     * Links the pits of a player together, with the last pit linking to the player's store pit.
     *
     * @param playerPits     the list of pits belonging to the player
     * @param playerOneStore the store pit belonging to the player
     */
    private static void linkPits(List<Pit> playerPits, Pit playerOneStore) {
        int size = playerPits.size();
        for (int i = 0; i < size; i++) {
            Pit currentPit = playerPits.get(i);
            Pit nextPit;
            if (i == size - 1) {
                nextPit = playerOneStore;
            } else {
                nextPit = playerPits.get(i + 1);
            }
            currentPit.setNextPit(nextPit);
        }
    }

    private MancalaGame prepareGame(Board board) {
        return gameRepository.save(new MancalaGame(board, List.of()));
    }
}

package com.mygame.mancala.unit.service;

import java.util.List;

import com.mygame.mancala.DTO.CreateMancalaGameParamsDto;
import com.mygame.mancala.model.Board;
import com.mygame.mancala.model.MancalaGame;
import com.mygame.mancala.repository.BoardRepository;
import com.mygame.mancala.repository.GameRepository;
import com.mygame.mancala.unit.MockitoUnitTest;
import com.mygame.mancala.service.MancalaGameCreationService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MancalaGameCreationServiceUnitTest extends MockitoUnitTest {
    @Mock
    private BoardRepository boardRepository;
    @Mock
    private GameRepository gameRepository;
    @InjectMocks
    private MancalaGameCreationService gameService;

    @Test
    public void testCreateGame() {
        int numberOfHouseStones = 4;
        var board = new Board(List.of());
        var game = new MancalaGame(board, List.of());

        when(boardRepository.save(any(Board.class))).thenReturn(board);
        when(gameRepository.save(any(MancalaGame.class))).thenReturn(game);

        var dto = new CreateMancalaGameParamsDto(numberOfHouseStones);
        MancalaGame createdGame = gameService.createGame(dto);

        assertNotNull(createdGame);
        assertEquals(board, createdGame.getBoard());
        assertEquals(List.of(), createdGame.getPlayers());

        verify(boardRepository, times(1)).save(any(Board.class));
        verify(gameRepository, times(1)).save(any(MancalaGame.class));
    }


}

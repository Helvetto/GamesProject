package com.mygame.mancala.unit.service;

import java.util.List;

import com.mygame.mancala.unit.MockitoUnitTest;
import com.mygame.mancala.model.Board;
import com.mygame.mancala.model.MancalaGame;
import com.mygame.mancala.model.MancalaGameStatus;
import com.mygame.mancala.model.pit.Pit;
import com.mygame.mancala.model.pit.PitType;
import com.mygame.mancala.repository.GameRepository;
import com.mygame.mancala.service.MancalaGamePlayService;
import com.mygame.mancala.service.MancalaSowService;
import com.mygame.model.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MancalaGamePlayServiceUnitTest extends MockitoUnitTest {

    @Mock
    private MancalaSowService sowService;

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private MancalaGamePlayService gamePlayService;


    private final Long gameId = 1L;
    private final Long pitId = 1L;
    private final Long playerId = 1L;

    private Board board;
    private Player player;
    private MancalaGame game;

    @BeforeEach
    void setUp() {
        sowService = mock(MancalaSowService.class);
        gameRepository = mock(GameRepository.class);
        gamePlayService = new MancalaGamePlayService(sowService, gameRepository);

        board = mock(Board.class);
        player = mock(Player.class);
        game = mock(MancalaGame.class);
    }

    @Test
    void whenSowing_thenGameShouldNotFinish() {
        var normalPit1 = new Pit(1, PitType.NORMAL);
        var normalPit2 = new Pit(0, PitType.NORMAL);
        var pits = List.of(normalPit1, normalPit2);

        normalPit1.setPlayer(player);
        normalPit2.setPlayer(player);
        when(board.getPits()).thenReturn(pits);
        when(sowService.sow(gameId, pitId, playerId)).thenReturn(game);
        when(game.getBoard()).thenReturn(board);

        gamePlayService.sow(gameId, pitId, playerId);

        verify(game, never()).setStatus(any());
        verify(gameRepository, never()).save(game);
    }

    @Test
    void whenSowing_thenGameShouldFinish() {
        var normalPit1 = new Pit(0, PitType.NORMAL);
        var normalPit2 = new Pit(0, PitType.NORMAL);
        var pits = List.of(normalPit1, normalPit2);

        normalPit1.setPlayer(player);
        normalPit2.setPlayer(player);
        when(board.getPits()).thenReturn(pits);
        when(sowService.sow(gameId, pitId, playerId)).thenReturn(game);
        when(game.getBoard()).thenReturn(board);

        gamePlayService.sow(gameId, pitId, playerId);

        verify(game, times(1)).setStatus(MancalaGameStatus.FINISHED);
        verify(gameRepository, times(1)).save(game);
    }
}


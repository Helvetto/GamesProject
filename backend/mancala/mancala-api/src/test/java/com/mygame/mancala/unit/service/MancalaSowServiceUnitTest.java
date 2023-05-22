package com.mygame.mancala.unit.service;

import java.util.List;

import com.mygame.exception.GameIllegalArgumentException;
import com.mygame.mancala.model.MancalaGameStatus;
import com.mygame.mancala.unit.MockitoUnitTest;
import com.mygame.mancala.model.Board;
import com.mygame.mancala.model.MancalaGame;
import com.mygame.mancala.model.pit.Pit;
import com.mygame.mancala.model.pit.PitType;
import com.mygame.mancala.repository.GameRepository;
import com.mygame.mancala.service.MancalaSowService;
import com.mygame.model.entity.Game;
import com.mygame.model.entity.Player;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MancalaSowServiceUnitTest extends MockitoUnitTest {

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private MancalaSowService sowService;

    private final Board board = mock(Board.class);
    private final Player playerOne = mock(Player.class);
    private final Player playerTwo = mock(Player.class);


    private final Long gameId = 1L;
    private final MancalaGame game = Mockito.mock(MancalaGame.class);

    @BeforeEach
    void init() {
        when(game.getStatus()).thenReturn(MancalaGameStatus.IN_PROGRESS);
    }

    @Test
    void testSowWithInvalidPitOwnership() {
        var normalPit1ForPlayerOneMock = mock(Pit.class);
        var normalPit1ForPlayerTwoMock = mock(Pit.class);
        var pits = List.of(
                normalPit1ForPlayerOneMock, normalPit1ForPlayerTwoMock
        );

        when(playerOne.getId()).thenReturn(1L);
        when(playerTwo.getId()).thenReturn(2L);
        when(normalPit1ForPlayerTwoMock.getPlayer()).thenReturn(playerTwo);
        when(normalPit1ForPlayerTwoMock.getId()).thenReturn(Long.valueOf(5));
        when(board.getPits()).thenReturn(pits);
        when(game.getPlayerTurn()).thenReturn(playerOne);
        when(game.getBoard()).thenReturn(board);
        when(gameRepository.findByIdOrThrow(gameId)).thenReturn(game);

        var exception = assertThrows(GameIllegalArgumentException.class, () ->
                sowService.sow(gameId, board.getPits().get(1).getId(), playerOne.getId()));

        assertTrue(exception.getMessage().contains("Cannot sow from opponent's pit"));
    }

    @Test
    void testSowWithInvalidGameStatus() {
        var normalPit1ForPlayerOneMock = mock(Pit.class);
        var normalPit2ForPlayerOneMock = mock(Pit.class);
        var normalPit3ForPlayerOneMock = mock(Pit.class);
        var normalPit4ForPlayerOneMock = mock(Pit.class);
        var mancalaPitForPlayerOneMock = mock(Pit.class);

        var normalPit1ForPlayerTwoMock = mock(Pit.class);
        var normalPit2ForPlayerTwoMock = mock(Pit.class);
        var normalPit3ForPlayerTwoMock = mock(Pit.class);
        var normalPit4ForPlayerTwoMock = mock(Pit.class);
        var mancalaPitForPlayerTwoMock = mock(Pit.class);

        var pits = List.of(
                normalPit1ForPlayerOneMock, normalPit2ForPlayerOneMock, normalPit3ForPlayerOneMock,
                normalPit4ForPlayerOneMock, mancalaPitForPlayerOneMock, normalPit1ForPlayerTwoMock,
                normalPit2ForPlayerTwoMock, normalPit3ForPlayerTwoMock, normalPit4ForPlayerTwoMock,
                mancalaPitForPlayerTwoMock
        );


        when(normalPit1ForPlayerTwoMock.getId()).thenReturn(Long.valueOf(5));
        when(board.getPits()).thenReturn(pits);
        when(gameRepository.findByIdOrThrow(gameId)).thenReturn(game);
        when(game.getStatus()).thenReturn(MancalaGameStatus.FINISHED);

        var exception = assertThrows(GameIllegalArgumentException.class, () ->
                sowService.sow(gameId, board.getPits().get(5).getId(), playerTwo.getId()));

        Assertions.assertThat(exception.getMessage()).contains("has invalid status");
    }

    @Test
    void testSowWithInvalidTurn() {
        var normalPit1ForPlayerOneMock = mock(Pit.class);
        var normalPit1ForPlayerTwoMock = mock(Pit.class);
        var pits = List.of(
                normalPit1ForPlayerOneMock, normalPit1ForPlayerTwoMock
        );

        when(playerOne.getId()).thenReturn(1L);
        when(playerTwo.getId()).thenReturn(2L);
        when(board.getPits()).thenReturn(pits);
        when(game.getPlayerTurn()).thenReturn(playerOne);
        when(gameRepository.findByIdOrThrow(gameId)).thenReturn(game);
        when(normalPit1ForPlayerTwoMock.getId()).thenReturn(11L);
        when(game.getBoard()).thenReturn(board);

        var exception = assertThrows(GameIllegalArgumentException.class, () ->
                sowService.sow(gameId, normalPit1ForPlayerTwoMock.getId(), playerTwo.getId()));

        assertEquals("Its not your turn now!", exception.getMessage());
    }

    @Test
    void testSowWithEmptyPit() {
        var normalPitForPlayerOneMock = mock(Pit.class);
        var normalPitForPlayerTwoMock = mock(Pit.class);
        var pits = List.of(
                normalPitForPlayerOneMock, normalPitForPlayerTwoMock
        );

        when(normalPitForPlayerOneMock.isEmpty()).thenReturn(true);
        when(playerOne.getId()).thenReturn(1L);
        when(playerTwo.getId()).thenReturn(2L);
        when(normalPitForPlayerOneMock.getPlayer()).thenReturn(playerOne);
        when(board.getPits()).thenReturn(pits);
        when(board.getPits()).thenReturn(pits);
        when(gameRepository.findByIdOrThrow(gameId)).thenReturn(game);
        when(playerOne.getId()).thenReturn(1L);
        when(game.getPlayerTurn()).thenReturn(playerOne);
        when(game.getBoard()).thenReturn(board);

        var exception = assertThrows(GameIllegalArgumentException.class, () ->
                sowService.sow(gameId, board.getPits().get(1).getId(), playerOne.getId()));

        assertTrue(exception.getMessage().contains("Cannot sow from empty pit"));
    }

    @Test
    void testSowWithMancalaPit() {
        var normalPitForPlayerOneMock = mock(Pit.class);
        var mancalaPitForPlayerTwoMock = mock(Pit.class);
        var pits = List.of(
                normalPitForPlayerOneMock, mancalaPitForPlayerTwoMock
        );

        when(mancalaPitForPlayerTwoMock.getType()).thenReturn(PitType.MANCALA);
        when(mancalaPitForPlayerTwoMock.getId()).thenReturn(11L);
        when(playerOne.getId()).thenReturn(1L);
        when(playerTwo.getId()).thenReturn(2L);
        when(board.getPits()).thenReturn(pits);
        when(board.getPits()).thenReturn(pits);
        when(gameRepository.findByIdOrThrow(gameId)).thenReturn(game);
        when(playerOne.getId()).thenReturn(1L);
        when(game.getPlayerTurn()).thenReturn(playerOne);
        when(game.getBoard()).thenReturn(board);

        var exception = assertThrows(GameIllegalArgumentException.class, () ->
                sowService.sow(gameId, mancalaPitForPlayerTwoMock.getId(), playerOne.getId()));

        assertTrue(exception.getMessage().contains("Cannot sow from mancala"));
    }

    @Test
    void testSowWithGrabbingOpponentStones() {
        var normalPit1ForPlayerOneMock = mock(Pit.class);
        var normalPit2ForPlayerOneMock = mock(Pit.class);
        var mancalaPitForPlayerOneMock = mock(Pit.class);

        var normalPit1ForPlayerTwoMock = mock(Pit.class);
        var normalPit2ForPlayerTwoMock = mock(Pit.class);
        var mancalaPitForPlayerTwoMock = mock(Pit.class);

        when(normalPit1ForPlayerOneMock.getPlayer()).thenReturn(playerOne);
        when(normalPit2ForPlayerOneMock.getPlayer()).thenReturn(playerOne);
        when(mancalaPitForPlayerOneMock.getPlayer()).thenReturn(playerOne);

        when(normalPit1ForPlayerTwoMock.getPlayer()).thenReturn(playerTwo);

        when(normalPit2ForPlayerTwoMock.getPlayer()).thenReturn(playerTwo);
        when(mancalaPitForPlayerTwoMock.getPlayer()).thenReturn(playerTwo);

        when(normalPit1ForPlayerOneMock.getType()).thenReturn(PitType.NORMAL);
        when(normalPit2ForPlayerOneMock.getType()).thenReturn(PitType.NORMAL);
        when(mancalaPitForPlayerOneMock.getType()).thenReturn(PitType.MANCALA);

        when(normalPit1ForPlayerTwoMock.getType()).thenReturn(PitType.NORMAL);
        when(normalPit2ForPlayerTwoMock.getType()).thenReturn(PitType.NORMAL);
        when(mancalaPitForPlayerTwoMock.getType()).thenReturn(PitType.MANCALA);

        when(normalPit1ForPlayerOneMock.getNextPit()).thenReturn(normalPit2ForPlayerOneMock);
        when(normalPit2ForPlayerOneMock.getNextPit()).thenReturn(mancalaPitForPlayerOneMock);
        when(mancalaPitForPlayerOneMock.getNextPit()).thenReturn(normalPit1ForPlayerTwoMock);

        when(normalPit1ForPlayerTwoMock.getNextPit()).thenReturn(normalPit2ForPlayerTwoMock);
        when(normalPit2ForPlayerTwoMock.getNextPit()).thenReturn(mancalaPitForPlayerTwoMock);
        when(mancalaPitForPlayerTwoMock.getNextPit()).thenReturn(normalPit1ForPlayerOneMock);

        var pits = List.of(
                normalPit1ForPlayerOneMock, normalPit2ForPlayerOneMock, mancalaPitForPlayerOneMock,
                normalPit1ForPlayerTwoMock,
                normalPit2ForPlayerTwoMock,
                mancalaPitForPlayerTwoMock
        );

        when(normalPit1ForPlayerOneMock.getStones()).thenReturn(0);
        when(normalPit1ForPlayerOneMock.getStones()).thenReturn(1);
        when(normalPit2ForPlayerOneMock.getStones()).thenReturn(4);
        when(normalPit2ForPlayerOneMock.getId()).thenReturn(1L);

        when(normalPit2ForPlayerTwoMock.getStones()).thenReturn(2);

        when(playerOne.getId()).thenReturn(1L);
        when(playerTwo.getId()).thenReturn(2L);
        when(board.getPits()).thenReturn(pits);
        when(board.getPits()).thenReturn(pits);
        when(gameRepository.findByIdOrThrow(gameId)).thenReturn(game);
        when(playerOne.getId()).thenReturn(1L);
        when(game.getPlayerTurn()).thenReturn(playerOne);
        when(game.getBoard()).thenReturn(board);

        when(game.getPlayers()).thenReturn(List.of(playerOne, playerTwo));
        when(gameRepository.save(any(MancalaGame.class))).thenAnswer(i -> i.getArguments()[0]);

        sowService.sow(gameId, board.getPits().get(1).getId(), playerOne.getId());

        verify(game).setPlayerTurn(playerTwo);
        verify(mancalaPitForPlayerOneMock).addStones(any());
    }
}

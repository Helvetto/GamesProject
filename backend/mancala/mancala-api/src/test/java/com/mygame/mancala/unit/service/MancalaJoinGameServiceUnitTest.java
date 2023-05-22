package com.mygame.mancala.unit.service;

import java.util.List;

import com.mygame.mancala.unit.MockitoUnitTest;
import com.mygame.mancala.model.Board;
import com.mygame.mancala.model.MancalaGame;
import com.mygame.mancala.model.MancalaGameStatus;
import com.mygame.mancala.model.pit.Pit;
import com.mygame.mancala.model.pit.PitType;
import com.mygame.mancala.repository.GameRepository;
import com.mygame.mancala.repository.PitRepository;
import com.mygame.mancala.service.MancalaJoinGameService;
import com.mygame.model.entity.Player;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import repository.PlayerRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MancalaJoinGameServiceUnitTest extends MockitoUnitTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private PitRepository pitRepository;

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private MancalaJoinGameService joinGameService;

    private final Board board = mock(Board.class);


    @BeforeEach
    public void setup() {
        var normalPit1 = new Pit(4, PitType.NORMAL);
        var normalPit2 = new Pit(4, PitType.NORMAL);
        var normalPit3 = new Pit(4, PitType.NORMAL);
        var normalPit4 = new Pit(4, PitType.NORMAL);
        var mancalaPit = new Pit(0, PitType.MANCALA);
        normalPit1.setNextPit(normalPit2);
        normalPit2.setNextPit(normalPit3);
        normalPit3.setNextPit(normalPit4);
        normalPit4.setNextPit(mancalaPit);
        mancalaPit.setNextPit(normalPit1);
        var pits = List.of(normalPit1, normalPit2, normalPit3, normalPit4, mancalaPit);
        when(board.getPits()).thenReturn(pits);
    }

    @Test
    public void testJoinGame() {
        var player = new Player();
        var game = new MancalaGame(board, List.of());
        long gameId = 1L;

        when(gameRepository.findByIdOrThrow(gameId)).thenReturn(game);
        when(gameRepository.save(any(MancalaGame.class))).thenAnswer(i -> i.getArguments()[0]);
        when(playerRepository.save(any(Player.class))).thenReturn(player);

        var result = joinGameService.joinGame(gameId);

        Assertions.assertEquals(game, result);
        Assertions.assertEquals(MancalaGameStatus.NOT_STARTED, result.getStatus());
        Assertions.assertEquals(1, result.getPlayers().size());
        Assertions.assertEquals(player, result.getPlayers().get(0));
        verify(gameRepository, times(1)).save(game);
    }

    @Test
    public void testJoinGameWithPlayerId() {
        var player1 = new Player();
        var player2 = new Player();
        var game = new MancalaGame(board, List.of(player1));
        long gameId = 1L;

        when(gameRepository.findByIdOrThrow(gameId)).thenReturn(game);
        when(gameRepository.save(any(MancalaGame.class))).thenAnswer(i -> i.getArguments()[0]);
        when(playerRepository.save(any(Player.class))).thenAnswer(i -> i.getArguments()[0]);
        when(playerRepository.findByIdOrThrow(any())).thenReturn(player2);

        var result = joinGameService.joinGame(gameId, 2L);

        Assertions.assertEquals(game, result);
        Assertions.assertEquals(MancalaGameStatus.IN_PROGRESS, result.getStatus());
        Assertions.assertEquals(2, result.getPlayers().size());
        Assertions.assertTrue(result.getPlayers().contains(player1));
        Assertions.assertTrue(result.getPlayers().contains(player2));
        verify(gameRepository, times(1)).save(game);
    }

    @Test
    public void testJoinGameWithSamePlayerId() {
        var player = mock(Player.class);
        var game = new MancalaGame(board, List.of(player));
        long gameId = 1L;

        when(player.getId()).thenReturn(1L);
        when(gameRepository.findByIdOrThrow(gameId)).thenReturn(game);

        var result = joinGameService.joinGame(gameId, 1L);

        Assertions.assertEquals(game, result);
        Assertions.assertEquals(MancalaGameStatus.NOT_STARTED, result.getStatus());
        Assertions.assertEquals(1, result.getPlayers().size());
        Assertions.assertTrue(result.getPlayers().contains(player));
        verify(gameRepository, times(0)).save(game);
    }

}

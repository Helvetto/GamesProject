package com.mygame.mancala.unit.facade;

import java.util.List;

import com.mygame.mancala.DTO.CreateMancalaGameParamsDto;
import com.mygame.mancala.facade.MancalaGameFacade;
import com.mygame.mancala.mapper.MancalaGameDtoMapper;
import com.mygame.mancala.model.MancalaGame;
import com.mygame.mancala.repository.GameRepository;
import com.mygame.mancala.service.MancalaGameCreationService;
import com.mygame.mancala.service.MancalaGamePlayService;
import com.mygame.mancala.service.MancalaJoinGameService;
import com.mygame.mancala.unit.MockitoUnitTest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MancalaGameFacadeUnitTest extends MockitoUnitTest {

    @Mock
    private MancalaGameCreationService gameCreationService;
    @Mock
    private MancalaJoinGameService joinGameService;
    @Mock
    private MancalaGamePlayService gamePlayService;
    @Mock
    private MancalaGameDtoMapper mapper;
    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private MancalaGameFacade facade;


    @Test
    public void testCreateAndJoinGame() {
        var dto = new CreateMancalaGameParamsDto(4);

        var game = mock(MancalaGame.class);
        when(game.getId()).thenReturn(1L);
        when(gameCreationService.createGame(any())).thenReturn(game);
        when(joinGameService.joinGame(anyLong(), anyLong())).thenReturn(game);

        facade.createAndJoinGame(dto, 1L);

        verify(gameCreationService).createGame(dto);
        verify(joinGameService).joinGame(any(Long.class), any(Long.class));
    }

    @Test
    public void testJoinGame() {
        var game = mock(MancalaGame.class);

        when(gameRepository.findByIdOrThrow(anyLong())).thenReturn(game);
        when(joinGameService.joinGame(anyLong(), anyLong())).thenReturn(game);
        when(gamePlayService.startIfNeeded(anyLong())).thenReturn(game);
        when(game.getPlayers()).thenReturn(List.of());

        facade.joinGameAndStartIfNeeded(1L, 2L);

        verify(joinGameService).joinGame(anyLong(), anyLong());
    }

    @Test
    public void testSow() {
        when(gamePlayService.sow(anyLong(), anyLong(), anyLong())).thenReturn(new MancalaGame());

        facade.sow(1L, 1L, 1L);

        verify(gamePlayService).sow(anyLong(), anyLong(), anyLong());
    }
}

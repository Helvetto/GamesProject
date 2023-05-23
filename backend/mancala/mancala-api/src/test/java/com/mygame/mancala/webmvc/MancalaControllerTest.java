package com.mygame.mancala.webmvc;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mygame.mancala.DTO.CreateMancalaGameParamsDto;
import com.mygame.mancala.DTO.GameStatusDto;
import com.mygame.mancala.DTO.MancalaGameDto;
import com.mygame.mancala.DTO.MancalaGameInfoDto;
import com.mygame.mancala.DTO.PlayerDto;
import com.mygame.mancala.controller.MancalaGameController;
import com.mygame.mancala.facade.MancalaGameFacade;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MancalaGameController.class)
public class MancalaControllerTest {

    private static final String SESSION_PLAYER_ID_ATTRIBUTE = "playerId";
    private static final String COOKIE_PLAYER_ID_ATTRIBUTE = "player_id";

    @Autowired
    private MockMvc mvc;
    @MockBean
    private MancalaGameFacade facade;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void testGetInfo() throws Exception {
        var players = List.of(new PlayerDto(1L, false, true));
        var responseDto = new MancalaGameDto(1L, players, new MancalaGameInfoDto(GameStatusDto.NOT_STARTED, null, false), null);

        when(facade.getInfo(any())).thenReturn(responseDto);

        mvc.perform(
                        get("/mancala/info")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("gameId", "1")
                                .session(new MockHttpSession())
                )
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void testCreateEndpoint() throws Exception {
        var requestBody = new CreateMancalaGameParamsDto(4);
        var players = List.of(new PlayerDto(1L, false, true));
        var responseDto = new MancalaGameDto(1L, players, new MancalaGameInfoDto(GameStatusDto.NOT_STARTED, null, false), null);

        when(facade.createAndJoinGame(any(), any())).thenReturn(responseDto);

        var result = mvc.perform(
                        post("/mancala/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(requestBody))
                                .session(new MockHttpSession())
                )
                .andExpect(status().isOk())
                .andExpect(cookie().exists(COOKIE_PLAYER_ID_ATTRIBUTE))
                .andReturn();

        MockHttpSession session = (MockHttpSession) result.getRequest().getSession();
        Long attributeValue = (Long) session.getAttribute(SESSION_PLAYER_ID_ATTRIBUTE);
        assertEquals(1, attributeValue);
    }

    @Test
    void testJoinEndpointWithNewPlayer() throws Exception {
        var players = List.of(
                new PlayerDto(1L, false, false),
                new PlayerDto(2L, false, true)
        );
        var responseDto = new MancalaGameDto(1L, players, new MancalaGameInfoDto(GameStatusDto.IN_PROGRESS, null, false), null);
        var mockHttpSession = new MockHttpSession();

        when(facade.joinGameAndStartIfNeeded(any(), any())).thenReturn(responseDto);


        var result = mvc.perform(
                        post("/mancala/join")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("gameId", "1")
                                .session(mockHttpSession)
                )
                .andExpect(status().isOk())
                .andExpect(cookie().exists(COOKIE_PLAYER_ID_ATTRIBUTE))
                .andReturn();

        MockHttpSession session = (MockHttpSession) result.getRequest().getSession();
        Long attributeValue = (Long) session.getAttribute(SESSION_PLAYER_ID_ATTRIBUTE);
        assertEquals(2, attributeValue);
    }

    @Test
    void testJoinEndpointWithExistingPlayer() throws Exception {
        var players = List.of(
                new PlayerDto(1L, false, false)
        );
        var responseDto = new MancalaGameDto(1L, players, new MancalaGameInfoDto(GameStatusDto.NOT_STARTED, null, false), null);
        var mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute("playerId", 1);

        when(facade.joinGameAndStartIfNeeded(any(), any())).thenReturn(responseDto);

        var result = mvc.perform(
                        post("/mancala/join")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("gameId", "1")
                                .cookie(new Cookie(COOKIE_PLAYER_ID_ATTRIBUTE, "1"))
                                .session(mockHttpSession)
                )
                .andExpect(status().isOk())
                .andExpect(cookie().exists(COOKIE_PLAYER_ID_ATTRIBUTE))
                .andReturn();

        MockHttpSession session = (MockHttpSession) result.getRequest().getSession();
        Long attributeValue = (Long) session.getAttribute(SESSION_PLAYER_ID_ATTRIBUTE);
        assertEquals(1, attributeValue);
    }

    @Test
    void testSowEndpoint() throws Exception {
        var players = List.of(
                new PlayerDto(1L, false, false),
                new PlayerDto(2L, false, false)
        );
        var responseDto = new MancalaGameDto(1L, players, new MancalaGameInfoDto(GameStatusDto.IN_PROGRESS, null, false), null);
        var mockHttpSession = new MockHttpSession();

        mockHttpSession.setAttribute(SESSION_PLAYER_ID_ATTRIBUTE, 1);

        when(facade.joinGameAndStartIfNeeded(any(), any())).thenReturn(responseDto);
        when(facade.sow(any(), any(), any())).thenReturn(responseDto);

        var result = mvc.perform(
                        post("/mancala/sow")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("gameId", "1")
                                .param("pitId", "1")
                                .cookie(new Cookie(COOKIE_PLAYER_ID_ATTRIBUTE, "1"))
                                .session(mockHttpSession)
                )
                .andExpect(status().isOk())
                .andExpect(cookie().exists("player_id"))
                .andReturn();

        MockHttpSession session = (MockHttpSession) result.getRequest().getSession();
        Long attributeValue = (Long) session.getAttribute(SESSION_PLAYER_ID_ATTRIBUTE);
        assertEquals(1, attributeValue);
    }

}

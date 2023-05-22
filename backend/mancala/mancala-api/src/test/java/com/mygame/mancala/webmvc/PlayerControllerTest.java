package com.mygame.mancala.webmvc;

import com.mygame.common.controller.PlayerController;
import com.mygame.common.service.PlayerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PlayerController.class)
public class PlayerControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private PlayerService playerService;

    private static final String SESSION_PLAYER_ID_ATTRIBUTE = "playerId";


    @Test
    void testRemoveEndpoint() throws Exception {
        var mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute(SESSION_PLAYER_ID_ATTRIBUTE, 1);

        var result = mvc.perform(
                        post("/player/removeOnClose")
                                .param("gameId", "1")
                                .session(mockHttpSession)
                )
                .andExpect(status().isOk())
                .andReturn();

        MockHttpSession session = (MockHttpSession) result.getRequest().getSession();
        Object playerIdAttribute = session.getAttribute(SESSION_PLAYER_ID_ATTRIBUTE);
        assertNull(playerIdAttribute);
    }

    @Test
    void testRemoveEndpointWithouPlayerId() throws Exception {
        var mockHttpSession = new MockHttpSession();

        mvc.perform(
                        post("/player/removeOnClose")
                                .param("gameId", "1")
                                .session(mockHttpSession)
                )
                .andExpect(status().isBadRequest());

    }

}

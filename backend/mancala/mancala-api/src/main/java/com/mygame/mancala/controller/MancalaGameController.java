package com.mygame.mancala.controller;

import com.mygame.common.util.ControllerHelper;
import com.mygame.mancala.DTO.CreateMancalaGameParamsDto;
import com.mygame.mancala.DTO.MancalaGameDto;
import com.mygame.mancala.DTO.PlayerDto;
import com.mygame.mancala.facade.MancalaGameFacade;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mancala")
public class MancalaGameController {

    private final MancalaGameFacade facade;

    @GetMapping("/info")
    ResponseEntity<MancalaGameDto> getInfo(
            @RequestParam Long gameId,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        var game = facade.getInfo(gameId);
        return getMancalaGameDtoResponseEntity(request, response, game);
    }

    @PostMapping("/create")
    ResponseEntity<MancalaGameDto> createAndJoinGame(
            @RequestBody CreateMancalaGameParamsDto createGameParamsDto,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        var game = facade.createAndJoinGame(createGameParamsDto);
        return getMancalaGameDtoResponseEntity(request, response, game);
    }

    @PostMapping("/join")
    ResponseEntity<MancalaGameDto> joinGame(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam Long gameId
    ) {
        var session = request.getSession();
        Long playerId = ControllerHelper.getPlayerIdSessionAttribute(session);
        var game = facade.joinGame(gameId, playerId);
        return getMancalaGameDtoResponseEntity(request, response, game);
    }

    @PostMapping("/sow")
    ResponseEntity<MancalaGameDto> sow(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam Long pitId,
            @RequestParam Long gameId
    ) {
        Long playerId = ControllerHelper.getPlayerIdSessionAttribute(request.getSession());
        var game = facade.sow(gameId, pitId, playerId);
        return getMancalaGameDtoResponseEntity(request, response, game);
    }

    private ResponseEntity<MancalaGameDto> getMancalaGameDtoResponseEntity(
            HttpServletRequest request,
            HttpServletResponse response,
            MancalaGameDto game
    ) {
        var createdPlayerId = game.players().stream()
                .filter(PlayerDto::addedToTheGame)
                .map(PlayerDto::id)
                .findFirst()
                .orElse(
                        ControllerHelper.getPlayerIdSessionAttribute(request.getSession())
                );
        if (createdPlayerId != null) {
            response.addCookie(new Cookie(ControllerHelper.COOKIE_PLAYER_ID_ATTRIBUTE, createdPlayerId.toString()));
        }
        request.getSession(true).setAttribute(ControllerHelper.SESSION_PLAYER_ID_ATTRIBUTE, createdPlayerId);
        return ResponseEntity.ok(game);
    }
}

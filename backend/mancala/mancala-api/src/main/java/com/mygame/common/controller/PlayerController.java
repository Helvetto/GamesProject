package com.mygame.common.controller;

import com.mygame.common.service.PlayerService;
import com.mygame.common.util.ControllerHelper;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/player")
public class PlayerController {
    private final PlayerService playerService;

    @PostMapping("/removeOnClose")
    public ResponseEntity removeOnClose(HttpSession session, @RequestParam Long gameId) {
        Long playerId = ControllerHelper.getPlayerIdSessionAttribute(session);
        if (playerId == null) {
          return ResponseEntity.badRequest().build();
        }
        playerService.remove(playerId, gameId);
        session.removeAttribute(ControllerHelper.SESSION_PLAYER_ID_ATTRIBUTE);
        return ResponseEntity.ok().build();
    }
}

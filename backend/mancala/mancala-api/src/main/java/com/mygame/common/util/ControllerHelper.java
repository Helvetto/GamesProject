package com.mygame.common.util;

import java.util.Optional;

import jakarta.servlet.http.HttpSession;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class ControllerHelper {
    public final String SESSION_PLAYER_ID_ATTRIBUTE = "playerId";
    public final String COOKIE_PLAYER_ID_ATTRIBUTE = "player_id";

    @Nullable
    public Long getPlayerIdSessionAttribute(HttpSession session) {
        return Optional.ofNullable(session.getAttribute(SESSION_PLAYER_ID_ATTRIBUTE))
                .map(Number.class::cast)
                .map(Number::longValue)
                .orElse(null);
    }
}

package com.mygame.common.util.validation;

import com.mygame.exception.GameIsFullException;
import com.mygame.model.entity.Game;
import lombok.experimental.UtilityClass;

@UtilityClass
public class JoinGameValidations {

    public void validateGame(Game game) {
        validateGameIsFull(game);
    }

    private static void validateGameIsFull(Game game) {
        if (game.isFull()) {
            throw new GameIsFullException(game.getId());
        }
    }

}

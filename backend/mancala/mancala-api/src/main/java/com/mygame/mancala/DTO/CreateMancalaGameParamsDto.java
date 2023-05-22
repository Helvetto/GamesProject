package com.mygame.mancala.DTO;

import com.mygame.exception.GameIllegalArgumentException;

public record CreateMancalaGameParamsDto(int numberOfStones) {
    public CreateMancalaGameParamsDto {
        if (numberOfStones < 4) {
            throw new GameIllegalArgumentException("Number of stones must be at least 4");
        }
    }
}
package com.mygame.mancala.DTO;

import org.jetbrains.annotations.Nullable;

public record MancalaGameInfoDto(GameStatusDto status,
                                 @Nullable PlayerDto winner,
                                 @Nullable PlayerDto playerTurn,
                                 boolean draw) {
}

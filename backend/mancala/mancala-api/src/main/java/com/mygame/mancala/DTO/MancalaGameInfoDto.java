package com.mygame.mancala.DTO;

import org.jetbrains.annotations.Nullable;

public record MancalaGameInfoDto(GameStatusDto status, @Nullable PlayerDto winner, boolean draw){
}

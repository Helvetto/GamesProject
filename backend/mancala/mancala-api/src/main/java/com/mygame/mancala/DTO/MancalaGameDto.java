package com.mygame.mancala.DTO;

import java.util.List;

public record MancalaGameDto(Long id, List<PlayerDto> players, MancalaGameInfoDto info, BoardDto board) {
}

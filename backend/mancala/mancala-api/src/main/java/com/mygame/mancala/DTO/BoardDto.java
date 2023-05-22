package com.mygame.mancala.DTO;

import java.util.List;

public record BoardDto(
        List<PitDto> pits
) {
}

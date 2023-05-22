package com.mygame.mancala.DTO;

public record PitDto(Long id, Long playerId, Integer stones, Long nextPitId, PitTypeDto type) {}


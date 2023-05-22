package com.mygame.mancala.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record PlayerDto(Long id, boolean isHisTurn, @JsonIgnore boolean addedToTheGame) {}


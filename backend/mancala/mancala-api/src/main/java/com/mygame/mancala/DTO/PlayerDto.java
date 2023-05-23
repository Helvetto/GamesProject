package com.mygame.mancala.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Represents a data transfer object for a player.
 *
 * @param id                The ID of the player.
 * @param addedToTheGame    Indicates whether the player has been added to the game.
 *                          This field is annotated with @JsonIgnore to exclude it from serialization.
 *                          It is needed temporarily to properly set the created player ID cookie,
 *                          but should be removed once Spring Security is implemented.
 */
public record PlayerDto(Long id, @JsonIgnore boolean addedToTheGame) {}


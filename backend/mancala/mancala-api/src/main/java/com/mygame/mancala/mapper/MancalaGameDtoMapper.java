package com.mygame.mancala.mapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import com.mygame.mancala.DTO.GameStatusDto;
import com.mygame.mancala.DTO.MancalaGameDto;
import com.mygame.mancala.DTO.MancalaGameInfoDto;
import com.mygame.mancala.DTO.PitDto;
import com.mygame.mancala.DTO.PlayerDto;
import com.mygame.mancala.model.Board;
import com.mygame.mancala.model.MancalaGame;
import com.mygame.mancala.model.MancalaGameStatus;
import com.mygame.mancala.model.pit.Pit;
import com.mygame.mancala.model.pit.PitType;
import com.mygame.model.entity.Player;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ValueMapping;

@Mapper(componentModel = "spring")
public interface MancalaGameDtoMapper {

    @Mapping(target = "players", expression = "java(getPlayers(game.getPlayers(), game.getPlayerTurn()))")
    @Mapping(target = "info", expression = "java(getInfo(game))")
    @Mapping(source = "board.pits", target = "board.pits", qualifiedByName = "mapPitToPitDto")
    MancalaGameDto map(MancalaGame game);

    @Mapping(target = "players",
            expression = "java(getPlayers(game.getPlayers(), newPlayers, game.getPlayerTurn()))"
    )
    @Mapping(target = "info", expression = "java(getInfo(game))")
    MancalaGameDto map(MancalaGame game, List<Player> newPlayers);

    @Mapping(target = "playerId", source = "player.id")
    @Mapping(target = "nextPitId", source = "nextPit.id")
    PitDto map(Pit pit);

    @Named("mapPitToPitDto")
    default List<PitDto> mapPitToPitDto(List<Pit> pits) {
        return pits.stream().map(this::map).toList();
    }

    default List<PlayerDto> getPlayers(List<Player> allPlayers, Player playerTurn) {
        return getPlayers(allPlayers, List.of(), playerTurn);
    }

    default MancalaGameInfoDto getInfo(MancalaGame game) {
        return new MancalaGameInfoDto(mapStatus(game), findWinner(game.getBoard()), isDraw(game.getBoard()));
    }

    /**
     * Returns a list of {@link PlayerDto} objects representing all players in the game, indicating whether
     * they have been added to the game and if it is their turn to play.
     *
     * @param allPlayers a list of all players in the game
     * @param newPlayers a list of players who have been recently added to the game
     * @param playerTurn the player whose turn it is to play
     * @return a list of {@link PlayerDto} objects representing all players in the game
     */
    default List<PlayerDto> getPlayers(List<Player> allPlayers, List<Player> newPlayers, Player playerTurn) {
        var newPlayerIds = newPlayers.stream().map(Player::getId).toList();
        return allPlayers.stream()
                .map(player -> getPlayerDto(playerTurn, newPlayerIds, player))
                .toList();
    }

    /**
     * Finds the winner of the Mancala game based on the number of stones in the Mancala pits.
     * The winner is determined by comparing the stone counts of the Mancala pits.
     * If there is only one pit with the maximum stone count, that player is declared the winner.
     *
     * @param board the game board containing the pits
     * @return the {@link PlayerDto} representing the winner, or {@code null} if there is no winner
     */
    default PlayerDto findWinner(Board board) {
        var nonEmptyMancalaPits = board.getPits().stream()
                .filter(pit -> pit.getType() == PitType.MANCALA)
                .filter(pit -> !pit.isEmpty())
                .toList();

        int maxStoneCount = nonEmptyMancalaPits.stream()
                .mapToInt(Pit::getStones)
                .max()
                .orElse(-1);

        var winningPits = nonEmptyMancalaPits.stream()
                .filter(pit -> pit.getStones() == maxStoneCount)
                .toList();

        if (winningPits.size() == 1) {
            Pit winningPit = winningPits.get(0);
            return getPlayerDto(null, List.of(), winningPit.getPlayer());
        } else {
            return null;
        }
    }

    /**
     * Checks if the Mancala game is a draw.
     * <p>
     * A draw occurs when all the Mancala pits have the same number of stones,
     * <p>
     * except when both Mancala pits are empty, indicating the game is not finished.
     *
     * @param board the game board containing the pits
     * @return {@code true} if the game is a draw, {@code false} otherwise
     */
    default boolean isDraw(Board board) {
        var mancalaPits = board.getPits().stream()
                .filter(pit -> pit.getType() == PitType.MANCALA)
                .toList();

        var emptyMancalaPits = mancalaPits.stream()
                .filter(Pit::isEmpty)
                .toList();
        if (emptyMancalaPits.size() == 2) {
            // Corner case - when both Mancala pits are empty, the game cannot be a draw
            // because the game is not finished yet
            return false;
        }

        return mancalaPits.stream()
                .map(Pit::getStones)
                .distinct()
                .count() == 1;
    }

    private static PlayerDto getPlayerDto(Player playerTurn, List<Long> newPlayerIds, Player player) {
        return new PlayerDto(
                player.getId(),
                Objects.equals(player.getId(), playerTurn == null ? null : playerTurn.getId()),
                newPlayerIds.contains(player.getId())
        );
    }

    default GameStatusDto mapStatus(MancalaGame game) {
        return GameStatusDto.valueOf(game.getStatus().name());
    }

}

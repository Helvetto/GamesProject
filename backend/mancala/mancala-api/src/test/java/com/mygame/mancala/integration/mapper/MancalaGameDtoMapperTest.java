package com.mygame.mancala.integration.mapper;

import java.util.Objects;
import java.util.stream.Collectors;

import com.mygame.mancala.DTO.CreateMancalaGameParamsDto;
import com.mygame.mancala.DTO.MancalaGameDto;
import com.mygame.mancala.DTO.PitDto;
import com.mygame.mancala.integration.IntegrationTest;
import com.mygame.mancala.mapper.MancalaGameDtoMapper;
import com.mygame.mancala.model.MancalaGame;
import com.mygame.mancala.model.pit.Pit;
import com.mygame.mancala.service.MancalaGameCreationService;
import com.mygame.mancala.service.MancalaJoinGameService;
import com.mygame.model.entity.Player;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import repository.PlayerRepository;

@RequiredArgsConstructor
public class MancalaGameDtoMapperTest extends IntegrationTest {

    private final MancalaGameDtoMapper mapper;

    private final MancalaGameCreationService gameCreationService;
    private final PlayerRepository playerRepository;
    private final MancalaJoinGameService joinGameService;

    @Test
    void shouldCorrectlyMap() {
        var playerOne = playerRepository.save(new Player());
        var playerTwo = playerRepository.save(new Player());

        var game = gameCreationService.createGame(new CreateMancalaGameParamsDto(4));
        game = joinGameService.joinGame(game.getId(), playerOne.getId());
        game = joinGameService.joinGame(game.getId(), playerTwo.getId());
        var result = mapper.map(game);

        Assertions.assertThat(result.id()).isEqualTo(game.getId());
        Assertions.assertThat(result.info().status().toString()).isEqualTo(game.getStatus().toString());

        assertPits(game, result);

        var resultPlayers = result.players();
        Assertions.assertThat(resultPlayers).usingRecursiveFieldByFieldElementComparatorOnFields("id").isEqualTo(game.getPlayers());
    }

    private static void assertPits(MancalaGame game, MancalaGameDto result) {
        var resultPits = result.board().pits();
        Assertions.assertThat(resultPits).usingRecursiveFieldByFieldElementComparatorOnFields("id").isEqualTo(game.getBoard().getPits());
        Assertions.assertThat(resultPits.stream().map(PitDto::playerId).toList())
                .isEqualTo(game.getBoard().getPits().stream().map(Pit::getPlayer).map(Player::getId).toList());
        Assertions.assertThat(resultPits.stream().map(PitDto::stones).toList())
                .isEqualTo(game.getBoard().getPits().stream().map(Pit::getStones).toList());
        Assertions.assertThat(resultPits.stream().map(PitDto::type).map(Objects::toString).toList())
                .isEqualTo(game.getBoard().getPits().stream().map(Pit::getType).map(Objects::toString).toList());
    }
}

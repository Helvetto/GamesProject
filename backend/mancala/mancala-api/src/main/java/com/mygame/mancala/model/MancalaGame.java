package com.mygame.mancala.model;

import java.util.List;

import com.mygame.model.entity.Game;
import com.mygame.model.entity.GameType;
import com.mygame.model.entity.Player;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "mancala_game")
@DiscriminatorValue("MANCALA")
public class MancalaGame extends Game {
    @OneToOne
    private Board board;

    @Setter
    @Enumerated(value = EnumType.STRING)
    private MancalaGameStatus status;

    @OneToOne
    @Setter
    private Player playerTurn;

    @Override
    public GameType getType() {
        return GameType.MANCALA;
    }

    @Override
    public int getMaxPlayerCapacity() {
        return 2;
    }

    @Override
    public boolean isFull() {
        return getPlayers().size() == 2;
    }

    public MancalaGame(Board board, List<Player> players) {
        super(players);
        this.board = board;
        this.status = MancalaGameStatus.NOT_STARTED;
    }
}

package com.mygame.mancala.model.pit;

import com.mygame.repository.BaseJpaEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.mygame.model.entity.Player;


@Entity
@NoArgsConstructor
@Getter
public class Pit extends BaseJpaEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Player player;

    @Enumerated(EnumType.STRING)
    private PitType type;

    @OneToOne
    @Setter
    private Pit nextPit;

    private Integer stones;

    public Boolean isEmpty() {
        return this.stones == 0;
    }

    public Pit(Integer stones, PitType type) {
        this.stones = stones;
        this.type = type;
    }

    public void clear() {
        this.stones = 0;
    }

    public void sow() {
        this.stones++;
    }


    public void addStones(Integer stones) {
        this.stones += stones;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}

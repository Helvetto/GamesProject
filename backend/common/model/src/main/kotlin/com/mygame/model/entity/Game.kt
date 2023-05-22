package com.mygame.model.entity

import com.mygame.repository.BaseJpaEntity
import jakarta.persistence.DiscriminatorColumn
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType
import jakarta.persistence.ManyToMany

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "type")
abstract class Game() : BaseJpaEntity<Long?>() {
    constructor(players: MutableList<Player>) : this() {
        this.players = players
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id: Long? = null

    @ManyToMany(mappedBy = "activeGames")
    open var players: List<Player> = mutableListOf()

    abstract val type: GameType?
    abstract val maxPlayerCapacity: Int
    abstract val isFull: Boolean

    fun addPlayer(player: Player?) {
        val newPlayers = ArrayList(players)
        newPlayers.add(player)
        players = newPlayers
    }

    override fun getId(): Long? {
        return id;
    }
}
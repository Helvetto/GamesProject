package com.mygame.model.entity

import com.mygame.repository.BaseJpaEntity
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany

@Entity
class Player : BaseJpaEntity<Long?>() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id: Long? = null

    @ManyToMany(targetEntity = Game::class)
    @JoinTable(
        name = "player_active_games",
        joinColumns = [JoinColumn(name = "player_id")],
        inverseJoinColumns = [JoinColumn(name = "game_id")]
    )
    var activeGames: Set<Game> = HashSet()

    fun addActiveGameIds(games: Set<Game>): Set<Game> {
        val updatedGameIds = HashSet(activeGames)
        updatedGameIds.addAll(games)
        activeGames = updatedGameIds
        return activeGames
    }

    fun removeActiveGameIds(games: Collection<Game>): Set<Game> {
        val updatedGameIds = HashSet(activeGames)
        games.let { updatedGameIds.removeAll(it.toSet()) }
        activeGames = updatedGameIds
        return activeGames
    }

    override fun getId(): Long? {
        return id
    }
}
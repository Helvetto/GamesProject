package com.mygame.unit

import com.mygame.model.entity.Game
import com.mygame.model.entity.GameType
import com.mygame.model.entity.Player
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock

class GameEntityUnitTest {

    @Test
    fun testConstructor() {
        val players = listOf(mock(Player::class.java), mock(Player::class.java))
        val game = object : Game(players.toMutableList()) {
            override val type: GameType?
                get() = null
            override val maxPlayerCapacity: Int
                get() = 2
            override val isFull: Boolean
                get() = players.size >= maxPlayerCapacity
        }

        assertEquals(players, game.players)
    }

    @Test
    fun testAddPlayer() {
        val player = mock(Player::class.java)
        val game = object : Game() {
            override val type: GameType?
                get() = null
            override val maxPlayerCapacity: Int
                get() = 2
            override val isFull: Boolean
                get() = players.size >= maxPlayerCapacity
        }

        game.addPlayer(player)

        assertEquals(1, game.players.size)
        assertTrue(game.players.contains(player))
    }

    @Test
    fun testGetId() {
        val game = object : Game() {
            override val type: GameType?
                get() = null
            override val maxPlayerCapacity: Int
                get() = 2
            override val isFull: Boolean
                get() = players.size >= maxPlayerCapacity
        }

        val id = game.id

        assertNull(id)
    }

    @Test
    fun testAddMultiplePlayers() {
        val player1 = mock(Player::class.java)
        val player2 = mock(Player::class.java)
        val game = object : Game() {
            override val type: GameType?
                get() = null
            override val maxPlayerCapacity: Int
                get() = 2
            override val isFull: Boolean
                get() = players.size >= maxPlayerCapacity
        }

        game.addPlayer(player1)
        game.addPlayer(player2)

        assertEquals(2, game.players.size)
        assertTrue(game.players.containsAll(listOf(player1, player2)))
    }

    @Test
    fun testIsFullWhenNotFull() {
        val player1 = mock(Player::class.java)
        val game = object : Game() {
            override val type: GameType?
                get() = null
            override val maxPlayerCapacity: Int
                get() = 2
            override val isFull: Boolean
                get() = players.size >= maxPlayerCapacity
        }

        game.addPlayer(player1)

        assertFalse(game.isFull)
    }

    @Test
    fun testIsFullWhenFull() {
        val player1 = mock(Player::class.java)
        val player2 = mock(Player::class.java)
        val game = object : Game() {
            override val type: GameType?
                get() = null
            override val maxPlayerCapacity: Int
                get() = 2
            override val isFull: Boolean
                get() = players.size >= maxPlayerCapacity
        }

        game.addPlayer(player1)
        game.addPlayer(player2)

        assertTrue(game.isFull)
    }
}

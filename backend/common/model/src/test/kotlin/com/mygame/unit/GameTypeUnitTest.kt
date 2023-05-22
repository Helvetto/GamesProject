package com.mygame.unit

import com.mygame.model.entity.Game
import com.mygame.model.entity.GameType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class GameTypeUnitTest {
    @Test
    fun testGetType() {
        val game = object : Game() {
            override val type: GameType
                get() = GameType.MANCALA
            override val maxPlayerCapacity: Int
                get() = 2
            override val isFull: Boolean
                get() = players.size >= maxPlayerCapacity
        }

        Assertions.assertEquals(GameType.MANCALA, game.type)
    }
}
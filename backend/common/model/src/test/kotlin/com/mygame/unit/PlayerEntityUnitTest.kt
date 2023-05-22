package com.mygame.unit

import com.mygame.model.entity.Game
import com.mygame.model.entity.Player
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.mockito.Mockito

class PlayerEntityUnitTest {

    @Test
    fun testAddActiveGameIds() {
        val player = Player()
        val game = Mockito.mock(Game::class.java)
        val gameIdsToAdd = setOf(game)

        val updatedGameIds = player.addActiveGameIds(gameIdsToAdd)

        assertEquals(gameIdsToAdd, updatedGameIds)
    }

    @Test
    fun testAddActiveGameIdsWithExistingGameIds() {
        val player = Player()
        val game = Mockito.mock(Game::class.java)
        player.activeGames = setOf(game)

        val gameToAdd = Mockito.mock(Game::class.java)
        val gameIdsToAdd = setOf(gameToAdd)

        val updatedGames = player.addActiveGameIds(gameIdsToAdd)
        assertEquals(setOf(game, gameToAdd), updatedGames)

    }


    @Test
    fun testGetId() {
        val player = Player()
        val id = player.id

        assertNull(id)
    }
}

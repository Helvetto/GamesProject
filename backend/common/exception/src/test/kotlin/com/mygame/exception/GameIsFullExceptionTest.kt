package com.mygame.exception

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class GameIsFullExceptionTest {

    @Test
    fun `constructor sets exception message correctly`() {
        val gameId = 123L
        val exception = assertThrows<GameIsFullException> { throw GameIsFullException(gameId) }
        assertEquals("Game $gameId is full", exception.message)
    }
}

package com.mygame.exception

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class GameIllegalArgumentExceptionTest {

    @Test
    fun `constructor with message throws exception with correct message`() {
        val message = "Invalid input: %s"
        val arg = "abc"

        val exception = assertThrows<GameIllegalArgumentException> {
            throw GameIllegalArgumentException(message, arg)
        }

        assertEquals(String.format(message, arg), exception.message)
    }

    @Test
    fun `constructor with formatted message throws exception with correct message`() {
        val message = "Invalid input: %s=%d"
        val arg1 = "abc"
        val arg2 = 123

        val exception = assertThrows<GameIllegalArgumentException> {
            throw GameIllegalArgumentException(message, arg1, arg2)
        }

        assertEquals(String.format(message, arg1, arg2), exception.message)
    }
}

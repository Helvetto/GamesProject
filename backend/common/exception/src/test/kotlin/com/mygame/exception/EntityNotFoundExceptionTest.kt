package com.mygame.exception

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class EntityNotFoundExceptionTest {
    @Test
    fun `should create EntityNotFoundException with entity name`() {
        val entityNotFoundException = EntityNotFoundException("test entity")
        assertEquals("Entity not found: test entity", entityNotFoundException.message)
    }

    @Test
    fun `should create EntityNotFoundException with null`() {
        val entityNotFoundException = EntityNotFoundException(null)
        assertEquals("Entity not found: null", entityNotFoundException.message)
    }

    @Test
    fun `should create EntityNotFoundException with entity name and id`() {
        val entityNotFoundException = EntityNotFoundException("test entity", 123)
        assertEquals("Entity not found: test entity[123]", entityNotFoundException.message)
    }

    @Test
    fun `should format entity name with special characters`() {
        val entityNotFoundException = EntityNotFoundException("test {} entity", 123)
        assertEquals("Entity not found: test {} entity[123]", entityNotFoundException.message)
    }
}

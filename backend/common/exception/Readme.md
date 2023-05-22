# Exception Module

The `exception` module provides common exception classes for handling game-related errors.

## GameException

The `GameException` class is an abstract exception class that serves as the base class for game-related exceptions. It
extends `RuntimeException` and provides common functionality for handling game-related errors. You can extend this class
to create custom exceptions specific to your game application.

## EntityNotFoundException

The `EntityNotFoundException` class is a specific exception that extends `GameException`. It represents an exception
thrown when an entity is not found. It provides constructors to specify the entity name and ID (if applicable) that was
not found.

## GameIllegalArgumentException

The GameIllegalArgumentException class is another specific exception that extends GameException. It represents an
exception thrown when an illegal argument is provided for a game operation. It accepts a message string and optional
arguments for formatting the message.

## GameIsFullException

The GameIsFullException class is a specific exception that extends GameException. It represents an exception
thrown when a game is already full and cannot accept more players. It includes the ID of the game that is full.


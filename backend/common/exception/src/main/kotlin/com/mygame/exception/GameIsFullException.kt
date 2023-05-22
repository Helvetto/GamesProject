package com.mygame.exception

class GameIsFullException(gameId: Long) : GameException("Game $gameId is full")
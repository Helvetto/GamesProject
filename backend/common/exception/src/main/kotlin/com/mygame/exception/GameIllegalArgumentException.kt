package com.mygame.exception

class GameIllegalArgumentException(message: String?, vararg args: Any?) : GameException(message, *args)
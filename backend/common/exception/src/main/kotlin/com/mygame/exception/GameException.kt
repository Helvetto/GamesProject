package com.mygame.exception

abstract class GameException(message: String?, vararg args: Any?) :
    RuntimeException(message?.let { String.format(it.replace("\\{}".toRegex(), "%s"), *args) })
package com.mygame.exception

class EntityNotFoundException : GameException {
    constructor(entityName: String?) : super("Entity not found: {}", entityName)
    constructor(entityName: String?, entityId: Any?) : super("Entity not found: {}[{}]", entityName, entityId)
}
package org.alexcawl.kotea.ui

public fun interface UiMapper<Entity, UiEntity> {
    public suspend fun mapUi(entity: Entity): UiEntity
}

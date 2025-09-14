@file:OptIn(ExperimentalUuidApi::class)

package app.linksheet.feature.scenario

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

@Entity(tableName = "scenario")
data class ScenarioEntity(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    val name: String,
    val position: Int,
    val referrerApp: String?
) {
    constructor(id: Uuid = Uuid.random(), name: String, position: Int, referrerApp: String?) : this(
        id.toJavaUuid(),
        name,
        position,
        referrerApp
    )
}

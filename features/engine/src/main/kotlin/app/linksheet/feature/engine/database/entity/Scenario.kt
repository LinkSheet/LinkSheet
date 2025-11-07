@file:OptIn(ExperimentalUuidApi::class)

package app.linksheet.feature.engine.database.entity

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

@Immutable
@Entity(tableName = "scenario")
data class Scenario(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    val name: String,
    val position: Int,
    val referrerApp: String?
) {
    val kotlinUuid by lazy { id.toKotlinUuid() }

    constructor(id: Uuid = Uuid.random(), name: String, position: Int, referrerApp: String?) : this(
        id.toJavaUuid(),
        name,
        position,
        referrerApp
    )
}

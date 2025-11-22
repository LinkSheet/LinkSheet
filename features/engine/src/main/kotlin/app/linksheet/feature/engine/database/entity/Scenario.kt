@file:OptIn(ExperimentalUuidApi::class)

package app.linksheet.feature.engine.database.entity

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.uuid.ExperimentalUuidApi

@Immutable
@Entity(tableName = "scenario")
data class Scenario(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    val name: String,
    val position: Int,
    val referrerApp: String?
) {
}

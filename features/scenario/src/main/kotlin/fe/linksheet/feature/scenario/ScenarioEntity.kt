package fe.linksheet.feature.scenario

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "scenario")
data class ScenarioEntity(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    val name: String,
    val position: Int,
    val referrerApp: String?
) {
}

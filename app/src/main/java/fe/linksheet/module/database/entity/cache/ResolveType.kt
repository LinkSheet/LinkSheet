package fe.linksheet.module.database.entity.cache

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "resolve_type"
)
data class ResolveType(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String
) {
}

package fe.linksheet.module.database.entity.cache

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "url")
data class UrlEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val url: String
) {
}

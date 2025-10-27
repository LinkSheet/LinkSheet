package app.linksheet.feature.engine.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "url")
data class UrlEntry(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val url: String
) {
}

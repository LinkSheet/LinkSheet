package app.linksheet.feature.engine.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "html_cache",
    foreignKeys = [
        ForeignKey(entity = UrlEntry::class, parentColumns = arrayOf("id"), childColumns = arrayOf("id"))
    ]
)
data class CachedHtml(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val content: String
) {
}

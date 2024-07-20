package fe.linksheet.module.database.entity.cache

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "html_cache",
    foreignKeys = [
        ForeignKey(entity = UrlEntity::class, parentColumns = arrayOf("id"), childColumns = arrayOf("id"))
    ]
)
data class HtmlCache(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val content: String
) {
}

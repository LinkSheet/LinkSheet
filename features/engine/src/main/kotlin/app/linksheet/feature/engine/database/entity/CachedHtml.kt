package app.linksheet.feature.engine.database.entity

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.PrimaryKey

@Entity(
    tableName = CachedHtml.TABLE_NAME,
    foreignKeys = [
        ForeignKey(entity = UrlEntry::class, parentColumns = arrayOf("id"), childColumns = arrayOf("id"))
    ]
)
data class CachedHtml(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val content: String
) {
    companion object{
        const val TABLE_NAME = "html_cache"
    }
}

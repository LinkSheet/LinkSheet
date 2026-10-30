package app.linksheet.feature.engine.database.entity

import androidx.room3.ColumnTypeConverters
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.PrimaryKey
import app.linksheet.feature.engine.core.fetcher.preview.PreviewFetchResultId

@Entity(
    tableName = PreviewCache.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = UrlEntry::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("id"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
@ColumnTypeConverters(value = [PreviewFetchResultId.Converter::class])
data class PreviewCache(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String?,
    val description: String?,
    val faviconUrl: String?,
    val thumbnailUrl: String?,
    val resultId: PreviewFetchResultId
//    val faviconType: String?,
//    val faviconUrl: ByteArray?,
//    val thumbnailType: String?,
//    val thumbnail: ByteArray?
) {
    companion object {
        const val TABLE_NAME = "preview_cache"
    }
}

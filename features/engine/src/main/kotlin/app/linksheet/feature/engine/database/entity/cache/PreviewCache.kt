package app.linksheet.feature.engine.database.entity.cache

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import app.linksheet.feature.engine.engine.fetcher.preview.PreviewFetchResultId

@Entity(
    tableName = "preview_cache",
    foreignKeys = [
        ForeignKey(entity = UrlEntry::class, parentColumns = arrayOf("id"), childColumns = arrayOf("id"))
    ]
)
@TypeConverters(value = [PreviewFetchResultId.Converter::class])
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
)

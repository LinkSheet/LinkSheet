package fe.linksheet.module.database.entity.cache

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "resolved_url", primaryKeys = ["urlId", "typeId"],
    foreignKeys = [
        ForeignKey(entity = UrlEntry::class, parentColumns = arrayOf("id"), childColumns = arrayOf("urlId")),
        ForeignKey(entity = ResolveType::class, parentColumns = arrayOf("id"), childColumns = arrayOf("typeId"))
    ]
)
data class ResolvedUrl(
    @ColumnInfo(index = true) val urlId: Long = 0,
    @ColumnInfo(index = true) val typeId: Long = 0,
    val result: String? = null
) {
}

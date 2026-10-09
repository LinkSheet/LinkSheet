package app.linksheet.feature.engine.database.entity

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey

@Entity(
    tableName = ResolvedUrl.TABLE_NAME, primaryKeys = ["urlId", "typeId"],
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
    companion object{
        const val TABLE_NAME = "resolved_url"
    }
}

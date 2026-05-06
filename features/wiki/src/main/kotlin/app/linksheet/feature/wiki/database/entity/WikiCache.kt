package app.linksheet.feature.wiki.database.entity

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey


@Entity(tableName = "wiki_cache")
data class WikiCache(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "_id") val id: Long = 0,
    @ColumnInfo(index = true) val url: String,
    val timestamp: Long,
    val text: String
)

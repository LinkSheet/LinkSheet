package app.linksheet.feature.browser.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "private_browsing_browser",
    indices = [Index("flatComponentName", unique = true)]
)
data class PrivateBrowsingBrowser(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "_id") val id: Int = 0,
    val flatComponentName: String
)

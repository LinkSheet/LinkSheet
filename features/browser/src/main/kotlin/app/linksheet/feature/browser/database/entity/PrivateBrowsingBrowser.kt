package app.linksheet.feature.browser.database.entity

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey

@Entity(
    tableName = "private_browsing_browser",
    indices = [Index("flatComponentName", unique = true)]
)
data class PrivateBrowsingBrowser(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "_id") val id: Int = 0,
    val flatComponentName: String
)

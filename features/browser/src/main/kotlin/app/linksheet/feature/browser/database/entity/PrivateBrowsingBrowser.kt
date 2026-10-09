package app.linksheet.feature.browser.database.entity

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey

@Entity(
    tableName = PrivateBrowsingBrowser.TABLE_NAME,
    indices = [Index(PrivateBrowsingBrowser.COLUMN_FLAT_COMPONENT_NAME, unique = true)]
)
data class PrivateBrowsingBrowser(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "_id") val id: Int = 0,
    @ColumnInfo(name = COLUMN_FLAT_COMPONENT_NAME) val flatComponentName: String
) {
    companion object {
        const val TABLE_NAME = "private_browsing_browser"
        const val COLUMN_FLAT_COMPONENT_NAME = "flatComponentName"
    }
}

package fe.linksheet.module.database.entity.whitelisted

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey

@Entity(tableName = WhitelistedInAppBrowser.TABLE_NAME, indices = [Index("packageName", unique = true)])
data class WhitelistedInAppBrowser(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "_id") val id: Int = 0,
    val packageName: String
) {
    companion object {
        const val TABLE_NAME = "whitelisted_in_app_browser"
    }
}

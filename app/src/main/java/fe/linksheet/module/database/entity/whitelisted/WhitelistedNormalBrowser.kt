package fe.linksheet.module.database.entity.whitelisted

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey

@Entity(tableName = "whitelisted_browser", indices = [Index("packageName", unique = true)])
data class WhitelistedNormalBrowser(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "_id") val id: Int = 0,
    val packageName: String
)

package fe.linksheet.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "whitelisted_browser", indices = [Index("packageName", unique = true)])
data class WhitelistedBrowser(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "_id") val id: Int = 0,
    val packageName: String
)

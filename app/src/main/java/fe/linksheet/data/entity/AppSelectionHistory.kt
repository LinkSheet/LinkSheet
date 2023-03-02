package fe.linksheet.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "app_selection_history",
    indices = [(Index("host", "lastUsed", unique = true))]
)
data class AppSelectionHistory(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "_id") val id: Int = 0,
    val host: String,
    val packageName: String,
    val lastUsed: Long,
)

package fe.linksheet.module.database.entity

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey

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

data class AppSelection(val packageName: String, val maxLastUsed: Long)

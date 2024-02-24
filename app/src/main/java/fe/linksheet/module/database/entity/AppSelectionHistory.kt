package fe.linksheet.module.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import fe.linksheet.module.log.impl.hasher.HostProcessor
import fe.linksheet.module.log.impl.hasher.LogDumpable
import fe.linksheet.module.log.impl.hasher.LogHasher
import fe.linksheet.module.log.impl.hasher.PackageProcessor
import fe.stringbuilder.util.commaSeparated
import fe.stringbuilder.util.curlyWrapped

@Entity(
    tableName = "app_selection_history",
    indices = [(Index("host", "lastUsed", unique = true))]
)
data class AppSelectionHistory(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "_id") val id: Int = 0,
    val host: String,
    val packageName: String,
    val lastUsed: Long,
) : LogDumpable {
    override fun dump(
        stringBuilder: StringBuilder,
        hasher: LogHasher
    ) = stringBuilder.curlyWrapped {
        commaSeparated {
            item { hasher.hash(stringBuilder, "host=", host, HostProcessor) }
            item { hasher.hash(stringBuilder, "packageName=", packageName, PackageProcessor) }
            item { append("lastUsed=", lastUsed) }
        }
    }
}

data class AppSelection(val packageName: String, val maxLastUsed: Long)

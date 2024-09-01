package fe.linksheet.module.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import fe.linksheet.module.redactor.HostProcessor
import fe.linksheet.module.redactor.PackageProcessor
import fe.linksheet.module.redactor.Redactable
import fe.linksheet.module.redactor.Redactor
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
) : Redactable<AppSelectionHistory> {
    override fun process(
        builder: StringBuilder,
        redactor: Redactor
    ) = builder.curlyWrapped {
        commaSeparated {
            item { redactor.process(builder, host, HostProcessor, "host=") }
            item { redactor.process(builder, packageName, PackageProcessor, "packageName=") }
            item { append("lastUsed=", lastUsed) }
        }
    }
}

data class AppSelection(val packageName: String, val maxLastUsed: Long)

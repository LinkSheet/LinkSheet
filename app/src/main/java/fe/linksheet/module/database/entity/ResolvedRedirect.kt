package fe.linksheet.module.database.entity

import androidx.room.Entity
import fe.linksheet.module.log.LogDumpable
import fe.linksheet.module.log.LogHasher
import fe.linksheet.module.log.UrlProcessor
import fe.stringbuilder.util.commaSeparated
import fe.stringbuilder.util.curlyWrapped

@Entity(tableName = "resolved_redirect", primaryKeys = ["shortUrl", "resolvedUrl"])
data class ResolvedRedirect(
    val shortUrl: String,
    val resolvedUrl: String
) : LogDumpable {
    override fun dump(
        stringBuilder: StringBuilder,
        hasher: LogHasher
    ) = stringBuilder.curlyWrapped {
        commaSeparated {
            item { hasher.hash(stringBuilder, "shortUrl=", shortUrl, UrlProcessor) }
            item { hasher.hash(stringBuilder, "resolvedUrl=", resolvedUrl, UrlProcessor) }
        }
    }
}
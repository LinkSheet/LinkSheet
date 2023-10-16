package fe.linksheet.module.database.entity.resolver

import androidx.room.Entity
import fe.linksheet.module.log.HashProcessor
import fe.linksheet.module.log.LogDumpable
import fe.linksheet.module.log.LogHasher
import fe.stringbuilder.util.commaSeparated
import fe.stringbuilder.util.curlyWrapped

@Entity(tableName = "amp2html_mapping", primaryKeys = ["ampUrl", "canonicalUrl"])
data class Amp2HtmlMapping(
    val ampUrl: String,
    val canonicalUrl: String
) : ResolverEntity<Amp2HtmlMapping>(), LogDumpable {

    override fun urlResolved() = canonicalUrl

    override fun dump(
        stringBuilder: StringBuilder,
        hasher: LogHasher
    ) = stringBuilder.curlyWrapped {
        commaSeparated {
            item { hasher.hash(stringBuilder, "ampUrl=", ampUrl, HashProcessor.UrlProcessor) }
            item { hasher.hash(stringBuilder, "canonicalUrl=", canonicalUrl,
                HashProcessor.UrlProcessor
            ) }
        }
    }
}
package fe.linksheet.module.database.entity.resolver

import androidx.room.Entity
import androidx.room.Ignore
import fe.linksheet.module.redactor.HashProcessor
import fe.linksheet.module.redactor.Redactable
import fe.linksheet.module.redactor.Redactor
import fe.stringbuilder.util.commaSeparated
import fe.stringbuilder.util.curlyWrapped

@Entity(tableName = "resolved_redirect", primaryKeys = ["shortUrl"])
data class ResolvedRedirect(
    val shortUrl: String,
    val resolvedUrl: String? = null
) : ResolverEntity<ResolvedRedirect>, Redactable<ResolvedRedirect> {
    @Ignore
    override val url: String? = resolvedUrl

    override fun process(builder: StringBuilder, redactor: Redactor): StringBuilder {
        return builder.curlyWrapped {
            commaSeparated {
                item { redactor.process(builder, shortUrl, HashProcessor.UrlProcessor, "shortUrl=") }
                item { redactor.process(builder, resolvedUrl, HashProcessor.UrlProcessor, "resolvedUrl=") }
            }
        }
    }
}

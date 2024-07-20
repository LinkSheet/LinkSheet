package fe.linksheet.module.database.entity.resolver

import androidx.room.Entity
import androidx.room.Ignore
import fe.linksheet.module.redactor.ProtectedStringBuilder
import fe.linksheet.module.redactor.Redactable
import fe.linksheet.module.redactor.Redactor
import fe.linksheet.module.redactor.StringUrl
import fe.stringbuilder.util.*

@Entity(
    tableName = "amp2html_mapping",
    primaryKeys = ["ampUrl"]
)
data class Amp2HtmlMapping(
    val ampUrl: String,
    val canonicalUrl: String? = null,
) : ResolverEntity<Amp2HtmlMapping>, Redactable<Amp2HtmlMapping> {
    @Ignore
    override val url: String? = canonicalUrl

    override fun buildString(builder: ProtectedStringBuilder) {
        builder.wrapped(Bracket.Curly) {
            separated(Separator.Comma) {
                item { sensitive("ampUrl", StringUrl(ampUrl)) }
                item {
                    if (canonicalUrl != null) {
                        sensitive("canonicalUrl", StringUrl(canonicalUrl))
                    } else {
                        append("canonicalUrl", "<null>")
                    }
                }
            }
        }
    }

    override fun process(builder: StringBuilder, redactor: Redactor): StringBuilder {
        return builder.curlyWrapped {
//            commaSeparated {
//                item {
//                    redactor.process(builder, ampUrl, HashProcessor.UrlProcessor, "ampUrl=")
//                }
//                item {
//                    redactor.process(builder, canonicalUrl, HashProcessor.UrlProcessor, "canonicalUrl=")
//                }
//            }
        }
    }
}

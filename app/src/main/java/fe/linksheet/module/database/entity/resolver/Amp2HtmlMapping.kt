package fe.linksheet.module.database.entity.resolver

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.Ignore

@Entity(
    tableName = "amp2html_mapping",
    primaryKeys = ["ampUrl"]
)
data class Amp2HtmlMapping(
    val ampUrl: String,
    val canonicalUrl: String? = null,
    @ColumnInfo(defaultValue = "'true'")
    val isCacheHit: Boolean = true
) : ResolverEntity<Amp2HtmlMapping>  {
    @Ignore
    override val url: String? = canonicalUrl
}

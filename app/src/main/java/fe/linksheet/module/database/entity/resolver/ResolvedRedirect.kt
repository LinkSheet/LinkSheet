package fe.linksheet.module.database.entity.resolver

import androidx.room3.Entity
import androidx.room3.Ignore

@Entity(tableName = "resolved_redirect", primaryKeys = ["shortUrl"])
data class ResolvedRedirect(
    val shortUrl: String,
    val resolvedUrl: String? = null
) : ResolverEntity<ResolvedRedirect>  {
    @Ignore
    override val url: String? = resolvedUrl
}

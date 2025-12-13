package fe.linksheet.module.database.entity.resolver

import androidx.room.Entity
import androidx.room.Ignore

@Entity(tableName = "resolved_redirect", primaryKeys = ["shortUrl"])
data class ResolvedRedirect(
    val shortUrl: String,
    val resolvedUrl: String? = null
) : ResolverEntity<ResolvedRedirect>  {
    @Ignore
    override val url: String? = resolvedUrl
}

package fe.linksheet.module.database.entity.resolver

import androidx.room3.Entity

@Entity(tableName = ResolvedRedirect.TABLE_NAME, primaryKeys = ["shortUrl"])
data class ResolvedRedirect(
    val shortUrl: String,
    val resolvedUrl: String? = null
) {
    companion object {
        const val TABLE_NAME = "resolved_redirect"
    }
}

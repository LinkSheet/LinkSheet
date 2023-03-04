package fe.linksheet.data.entity

import androidx.room.Entity

@Entity(tableName = "resolved_redirect", primaryKeys = ["shortUrl", "resolvedUrl"])
data class ResolvedRedirect(
    val shortUrl: String,
    val resolvedUrl: String
)
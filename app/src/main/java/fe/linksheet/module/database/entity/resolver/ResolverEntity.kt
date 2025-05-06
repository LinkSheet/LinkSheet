package fe.linksheet.module.database.entity.resolver

import androidx.room.Ignore

interface ResolverEntity<T> {
    @get:Ignore
    val url: String?
}

package fe.linksheet.module.database.entity.resolver

import androidx.room3.Ignore

interface ResolverEntity<T> {
    @get:Ignore
    val url: String?
}

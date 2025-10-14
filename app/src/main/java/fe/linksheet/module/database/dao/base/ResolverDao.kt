package fe.linksheet.module.database.dao.base

import androidx.room.Query
import app.linksheet.api.database.BaseDao
import fe.linksheet.module.database.entity.resolver.ResolverEntity

abstract interface ResolverDao<T : ResolverEntity<T>> : BaseDao<T> {
    @Query("")
    abstract fun getForInputUrl(inputUrl: String): T?
}

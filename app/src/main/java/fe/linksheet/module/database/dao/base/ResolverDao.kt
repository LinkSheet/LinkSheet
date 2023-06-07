package fe.linksheet.module.database.dao.base

import androidx.room.Query
import fe.linksheet.module.database.entity.resolver.ResolverEntity
import kotlinx.coroutines.flow.Flow

abstract interface ResolverDao<T : ResolverEntity<T>> : BaseDao<T> {
    @Query("")
    abstract fun getForInputUrl(inputUrl: String): Flow<T?>
}
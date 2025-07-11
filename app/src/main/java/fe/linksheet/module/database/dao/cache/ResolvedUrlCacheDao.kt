package fe.linksheet.module.database.dao.cache

import androidx.room.Dao
import androidx.room.Query
import fe.linksheet.module.database.dao.base.BaseDao
import fe.linksheet.module.database.entity.cache.ResolveType
import fe.linksheet.module.database.entity.cache.ResolvedUrl

@Dao
interface ResolvedUrlCacheDao : BaseDao<ResolvedUrl> {
    @Query("SELECT * FROM resolved_url WHERE urlId = :urlId AND typeId = :resolveTypeId")
    suspend fun getResolved(urlId: Long, resolveTypeId: Long): ResolvedUrl?
}

package app.linksheet.feature.engine.database.dao.cache

import androidx.room.Dao
import androidx.room.Query
import app.linksheet.api.database.BaseDao
import app.linksheet.feature.engine.database.entity.cache.ResolvedUrl

@Dao
interface ResolvedUrlCacheDao : BaseDao<ResolvedUrl> {
    @Query("SELECT * FROM resolved_url WHERE urlId = :urlId AND typeId = :resolveTypeId")
    suspend fun getResolved(urlId: Long, resolveTypeId: Long): ResolvedUrl?
}

package fe.linksheet.module.database.dao.cache

import androidx.room.Dao
import androidx.room.Query
import app.linksheet.api.database.BaseDao
import fe.linksheet.module.database.entity.cache.CachedHtml

@Dao
interface HtmlCacheDao : BaseDao<CachedHtml> {
    @Query("SELECT * FROM html_cache WHERE id = :urlId")
    suspend fun getCachedHtml(urlId: Long): CachedHtml?
}

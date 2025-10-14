package fe.linksheet.module.database.dao.cache

import androidx.room.Dao
import androidx.room.Query
import app.linksheet.api.database.BaseDao
import fe.linksheet.module.database.entity.cache.PreviewCache

@Dao
interface PreviewCacheDao : BaseDao<PreviewCache> {
    @Query("SELECT * FROM preview_cache WHERE id = :urlId")
    suspend fun getPreviewCache(urlId: Long): PreviewCache?
}

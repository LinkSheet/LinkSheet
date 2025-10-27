package app.linksheet.feature.engine.database.dao

import androidx.room.Dao
import androidx.room.Query
import app.linksheet.api.database.BaseDao
import app.linksheet.feature.engine.database.entity.PreviewCache

@Dao
interface PreviewCacheDao : BaseDao<PreviewCache> {
    @Query("SELECT * FROM preview_cache WHERE id = :urlId")
    suspend fun getPreviewCache(urlId: Long): PreviewCache?
}

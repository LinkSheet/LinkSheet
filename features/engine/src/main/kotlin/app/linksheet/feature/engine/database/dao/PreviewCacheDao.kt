package app.linksheet.feature.engine.database.dao

import androidx.room3.Dao
import androidx.room3.Query
import app.linksheet.api.database.BaseDao
import app.linksheet.api.database.UserDataDao
import app.linksheet.feature.engine.database.entity.PreviewCache
import kotlinx.coroutines.flow.Flow

@Dao
interface PreviewCacheDao : BaseDao<PreviewCache>, UserDataDao {
    @Query("SELECT * FROM ${PreviewCache.TABLE_NAME}")
    override fun getAll(): Flow<List<PreviewCache>>
    @Query("DELETE FROM ${PreviewCache.TABLE_NAME}")
    override suspend fun deleteAll()
    @Query("SELECT * FROM ${PreviewCache.TABLE_NAME} WHERE id = :urlId")
    suspend fun getPreviewCache(urlId: Long): PreviewCache?
}

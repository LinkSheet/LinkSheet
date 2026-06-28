package app.linksheet.feature.engine.database.dao

import androidx.room3.Dao
import androidx.room3.Query
import app.linksheet.api.database.BaseDao
import app.linksheet.api.database.UserDataDao
import app.linksheet.feature.engine.database.entity.CachedHtml
import kotlinx.coroutines.flow.Flow

@Dao
interface HtmlCacheDao : BaseDao<CachedHtml>, UserDataDao {
    @Query("SELECT * FROM ${CachedHtml.TABLE_NAME}")
    override fun getAll(): Flow<List<CachedHtml>>
    @Query("DELETE FROM ${CachedHtml.TABLE_NAME}")
    override suspend fun deleteAll()
    @Query("SELECT * FROM ${CachedHtml.TABLE_NAME} WHERE id = :urlId")
    suspend fun getCachedHtml(urlId: Long): CachedHtml?
}

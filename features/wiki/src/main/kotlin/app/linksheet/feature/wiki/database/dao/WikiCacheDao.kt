package app.linksheet.feature.wiki.database.dao

import androidx.room3.Dao
import androidx.room3.Query
import app.linksheet.api.database.BaseDao
import app.linksheet.api.database.UserDataDao
import app.linksheet.feature.wiki.database.entity.WikiCache
import kotlinx.coroutines.flow.Flow

@Dao
interface WikiCacheDao : BaseDao<WikiCache>, UserDataDao {
    @Query("SELECT * FROM ${WikiCache.TABLE_NAME}")
    override fun getAll(): Flow<List<WikiCache>>
    @Query("DELETE FROM ${WikiCache.TABLE_NAME}")
    override suspend fun deleteAll()
    @Query("SELECT * FROM ${WikiCache.TABLE_NAME} WHERE url = :url AND timestamp")
    suspend fun getCachedText(url: String): WikiCache?
}

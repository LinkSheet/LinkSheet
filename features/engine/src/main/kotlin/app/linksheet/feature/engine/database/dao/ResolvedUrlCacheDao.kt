package app.linksheet.feature.engine.database.dao

import androidx.room3.Dao
import androidx.room3.Query
import app.linksheet.api.database.BaseDao
import app.linksheet.api.database.UserDataDao
import app.linksheet.feature.engine.database.entity.ResolvedUrl
import kotlinx.coroutines.flow.Flow

@Dao
interface ResolvedUrlCacheDao : BaseDao<ResolvedUrl>, UserDataDao {
    @Query("SELECT * FROM ${ResolvedUrl.TABLE_NAME}")
    override fun getAll(): Flow<List<ResolvedUrl>>
    @Query("DELETE FROM ${ResolvedUrl.TABLE_NAME}")
    override suspend fun deleteAll()
    @Query("SELECT * FROM ${ResolvedUrl.TABLE_NAME} WHERE urlId = :urlId AND typeId = :resolveTypeId")
    suspend fun getResolved(urlId: Long, resolveTypeId: Long): ResolvedUrl?
}

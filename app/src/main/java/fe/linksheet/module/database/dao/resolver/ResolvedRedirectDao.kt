package fe.linksheet.module.database.dao.resolver

import androidx.room3.Dao
import androidx.room3.Query
import app.linksheet.api.database.BaseDao
import app.linksheet.api.database.UserDataDao
import fe.linksheet.module.database.entity.resolver.ResolvedRedirect
import kotlinx.coroutines.flow.Flow

@Dao
interface ResolvedRedirectDao : BaseDao<ResolvedRedirect>, UserDataDao {
    @Query("SELECT * FROM ${ResolvedRedirect.TABLE_NAME}")
    override fun getAll(): Flow<List<ResolvedRedirect>>
    @Query("DELETE FROM ${ResolvedRedirect.TABLE_NAME}")
    override suspend fun deleteAll()
    @Query("SELECT * FROM ${ResolvedRedirect.TABLE_NAME} WHERE shortUrl = :inputUrl")
    fun getForInputUrl(inputUrl: String): ResolvedRedirect?
}

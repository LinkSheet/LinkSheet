package app.linksheet.feature.libredirect.database.dao

import androidx.room3.Dao
import androidx.room3.Query
import app.linksheet.api.database.BaseDao
import app.linksheet.api.database.UserDataDao
import app.linksheet.feature.libredirect.database.entity.LibRedirectUserInstance
import kotlinx.coroutines.flow.Flow

@Dao
interface LibRedirectUserInstanceDao : BaseDao<LibRedirectUserInstance>, UserDataDao {
    @Query("SELECT * FROM ${LibRedirectUserInstance.TABLE_NAME}")
    override fun getAll(): Flow<List<LibRedirectUserInstance>>
    @Query("DELETE FROM ${LibRedirectUserInstance.TABLE_NAME}")
    override suspend fun deleteAll()
    @Query("SELECT * FROM ${LibRedirectUserInstance.TABLE_NAME} WHERE serviceKey = :serviceKey AND frontendKey = :frontend")
    fun getByServiceAndFrontendOrNull(serviceKey: String, frontend: String): Flow<List<LibRedirectUserInstance>>
}

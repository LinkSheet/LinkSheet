package app.linksheet.feature.libredirect.database.dao

import androidx.room3.Dao
import androidx.room3.Query
import app.linksheet.api.database.BaseDao
import app.linksheet.api.database.UserDataDao
import app.linksheet.feature.libredirect.database.entity.LibRedirectServiceState
import kotlinx.coroutines.flow.Flow


@Dao
interface LibRedirectServiceStateDao : BaseDao<LibRedirectServiceState>, UserDataDao {
    @Query("SELECT * FROM ${LibRedirectServiceState.TABLE_NAME}")
    override fun getAll(): Flow<List<LibRedirectServiceState>>
    @Query("DELETE FROM ${LibRedirectServiceState.TABLE_NAME}")
    override suspend fun deleteAll()
    @Query("SELECT * FROM ${LibRedirectServiceState.TABLE_NAME} WHERE serviceKey = :serviceKey")
    fun getServiceState(serviceKey: String): Flow<LibRedirectServiceState?>
}

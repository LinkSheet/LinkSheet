package app.linksheet.feature.libredirect.database.dao

import androidx.room3.Dao
import androidx.room3.Query
import app.linksheet.api.database.BaseDao
import app.linksheet.api.database.UserDataDao
import app.linksheet.feature.libredirect.database.entity.LibRedirectDefault
import kotlinx.coroutines.flow.Flow

@Dao
interface LibRedirectDefaultDao : BaseDao<LibRedirectDefault>, UserDataDao {
    @Query("SELECT * FROM ${LibRedirectDefault.TABLE_NAME}")
    override fun getAll(): Flow<List<LibRedirectDefault>>
    @Query("DELETE FROM ${LibRedirectDefault.TABLE_NAME}")
    override suspend fun deleteAll()
    @Query("SELECT * FROM ${LibRedirectDefault.TABLE_NAME} WHERE serviceKey = :serviceKey")
    fun getByServiceKey(serviceKey: String): Flow<LibRedirectDefault?>
}

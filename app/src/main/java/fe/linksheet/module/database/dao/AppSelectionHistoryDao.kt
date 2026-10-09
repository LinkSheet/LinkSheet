package fe.linksheet.module.database.dao

import androidx.room3.Dao
import androidx.room3.Query
import app.linksheet.api.database.BaseDao
import app.linksheet.api.database.UserDataDao
import fe.linksheet.module.database.entity.AppSelection
import fe.linksheet.module.database.entity.AppSelectionHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface AppSelectionHistoryDao : BaseDao<AppSelectionHistory>, UserDataDao {
    @Query("SELECT * FROM ${AppSelectionHistory.TABLE_NAME}")
    override fun getAll(): Flow<List<AppSelectionHistory>>

    @Query("DELETE FROM ${AppSelectionHistory.TABLE_NAME}")
    override suspend fun deleteAll()

    @Query("SELECT * FROM ${AppSelectionHistory.TABLE_NAME} WHERE host = :host")
    fun getByHost(host: String): Flow<List<AppSelectionHistory>>

    @Query("SELECT packageName, MAX(lastUsed) as maxLastUsed FROM ${AppSelectionHistory.TABLE_NAME} WHERE host = :host GROUP BY packageName")
    fun getLastUsedForHostGroupedByPackage(host: String): Flow<List<AppSelection>>

    @Query("DELETE FROM ${AppSelectionHistory.TABLE_NAME} WHERE host = :host")
    suspend fun deleteByHost(host: String)

    @Query("DELETE FROM ${AppSelectionHistory.TABLE_NAME} WHERE packageName = :packageName")
    suspend fun deleteByPackageName(packageName: String)

    @Query("DELETE FROM ${AppSelectionHistory.TABLE_NAME} WHERE packageName IN (:packageNames)")
    suspend fun deleteByPackageNames(packageNames: List<String>)
}

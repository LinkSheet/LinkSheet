package fe.linksheet.module.database.dao

import androidx.room.Dao
import androidx.room.Query
import fe.linksheet.common.dao.base.BaseDao
import fe.linksheet.module.database.entity.AppSelection
import fe.linksheet.module.database.entity.AppSelectionHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface AppSelectionHistoryDao : BaseDao<AppSelectionHistory> {
    @Query("SELECT * FROM app_selection_history WHERE host = :host")
    fun getByHost(host: String): Flow<List<AppSelectionHistory>>

    @Query("SELECT packageName, MAX(lastUsed) as maxLastUsed FROM app_selection_history WHERE host = :host GROUP BY packageName")
    fun getLastUsedForHostGroupedByPackage(host: String): Flow<List<AppSelection>>

    @Query("DELETE FROM app_selection_history WHERE host = :host")
    suspend fun deleteByHost(host: String)

    @Query("DELETE FROM app_selection_history WHERE packageName = :packageName")
    suspend fun deleteByPackageName(packageName: String)

    @Query("DELETE FROM app_selection_history WHERE packageName IN (:packageNames)")
    suspend fun deleteByPackageNames(packageNames: List<String>)
}

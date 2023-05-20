package fe.linksheet.module.database.dao

import androidx.room.Dao
import androidx.room.Query
import fe.linksheet.module.database.dao.base.BaseDao
import fe.linksheet.module.database.entity.AppSelectionHistory

@Dao
interface AppSelectionHistoryDao : BaseDao<AppSelectionHistory> {
    @Query("SELECT * FROM app_selection_history WHERE host = :host")
    fun getByHost(host: String): List<AppSelectionHistory>

    @Query("DELETE FROM app_selection_history WHERE host = :host")
    fun deleteByHost(host: String)

    @Query("DELETE FROM app_selection_history WHERE packageName = :packageName")
    fun deleteByPackageName(packageName: String)
}

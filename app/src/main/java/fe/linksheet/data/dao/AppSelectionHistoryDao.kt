package fe.linksheet.data.dao

import androidx.room.Dao
import androidx.room.Query
import fe.linksheet.data.dao.base.BaseDao
import fe.linksheet.data.entity.AppSelectionHistory

@Dao
interface AppSelectionHistoryDao : BaseDao<AppSelectionHistory> {
    @Query("SELECT * FROM app_selection_history WHERE host = :host")
    fun getByHost(host: String): List<AppSelectionHistory>

    @Query("DELETE FROM app_selection_history WHERE host = :host")
    fun deleteByHost(host: String)

    @Query("DELETE FROM app_selection_history WHERE packageName = :packageName")
    fun deleteByPackageName(packageName: String)
}

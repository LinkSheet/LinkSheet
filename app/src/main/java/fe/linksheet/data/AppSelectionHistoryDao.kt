package fe.linksheet.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AppSelectionHistoryDao {


    @Query("SELECT * FROM app_selection_history WHERE host = :host")
    fun historyForHost(host: String): List<AppSelectionHistory>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(preferredApp: AppSelectionHistory)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(preferredApps: List<AppSelectionHistory>)

    @Query("DELETE FROM app_selection_history WHERE host = :host")
    fun deleteHost(host: String)

    @Query("DELETE FROM app_selection_history WHERE packageName = :packageName")
    fun deleteByPackageName(packageName: String)
}

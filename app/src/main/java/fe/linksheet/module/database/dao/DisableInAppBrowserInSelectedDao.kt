package fe.linksheet.module.database.dao

import androidx.room3.Dao
import androidx.room3.Query
import app.linksheet.api.database.BaseDao
import fe.linksheet.module.database.entity.DisableInAppBrowserInSelected
import kotlinx.coroutines.flow.Flow

@Dao
interface DisableInAppBrowserInSelectedDao : BaseDao<DisableInAppBrowserInSelected> {
    @Query("SELECT * FROM disable_in_app_browser_in_selected")
    fun getAll(): Flow<List<DisableInAppBrowserInSelected>>

    @Query("DELETE FROM disable_in_app_browser_in_selected WHERE packageName = :packageName")
    suspend fun deleteByPackageOrComponentName(packageName: String)
}

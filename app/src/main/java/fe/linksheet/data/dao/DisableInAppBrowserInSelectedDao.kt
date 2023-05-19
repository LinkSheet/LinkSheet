package fe.linksheet.data.dao

import androidx.room.Dao
import androidx.room.Query
import fe.linksheet.data.dao.base.PackageEntityDao
import fe.linksheet.data.entity.DisableInAppBrowserInSelected
import kotlinx.coroutines.flow.Flow

@Dao
abstract class DisableInAppBrowserInSelectedDao : PackageEntityDao<DisableInAppBrowserInSelected, DisableInAppBrowserInSelected.Creator>(
    DisableInAppBrowserInSelected.Creator
) {

    @Query("SELECT * FROM disable_in_app_browser_in_selected")
    abstract fun getAll(): Flow<List<DisableInAppBrowserInSelected>>

    @Query("DELETE FROM disable_in_app_browser_in_selected WHERE packageName = :packageName")
    abstract override suspend fun deleteByPackageName(packageName: String)
}
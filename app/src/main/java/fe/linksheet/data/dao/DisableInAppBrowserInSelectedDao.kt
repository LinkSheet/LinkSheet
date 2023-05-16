package fe.linksheet.data.dao

import androidx.room.Dao
import androidx.room.Query
import fe.linksheet.data.entity.DisableInAppBrowserInSelected

@Dao
abstract class DisableInAppBrowserInSelectedDao : PackageEntityDao<DisableInAppBrowserInSelected, DisableInAppBrowserInSelected.Creator>(
    DisableInAppBrowserInSelected.Creator
) {
    @Query("SELECT * FROM disable_in_app_browser_in_selected")
    abstract fun getAll(): List<DisableInAppBrowserInSelected>

    @Query("DELETE FROM disable_in_app_browser_in_selected WHERE packageName = :packageName")
    abstract override fun deleteByPackageName(packageName: String)
}
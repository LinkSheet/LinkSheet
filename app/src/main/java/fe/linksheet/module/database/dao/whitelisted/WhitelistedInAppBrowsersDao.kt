package fe.linksheet.module.database.dao.whitelisted

import androidx.room3.Dao
import androidx.room3.Query
import app.linksheet.api.database.BaseDao
import fe.linksheet.module.database.entity.whitelisted.WhitelistedInAppBrowser
import kotlinx.coroutines.flow.Flow

@Dao
interface WhitelistedInAppBrowsersDao : BaseDao<WhitelistedInAppBrowser> {
    @Query("SELECT * FROM whitelisted_in_app_browser")
    fun getAll(): Flow<List<WhitelistedInAppBrowser>>

    @Query("DELETE FROM whitelisted_in_app_browser WHERE packageName = :packageName")
    suspend fun deleteByPackageOrComponentName(packageName: String)
}

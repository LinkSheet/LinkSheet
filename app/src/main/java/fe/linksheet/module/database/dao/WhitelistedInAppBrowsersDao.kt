package fe.linksheet.module.database.dao

import androidx.room.Dao
import androidx.room.Query
import fe.linksheet.module.database.dao.base.WhitelistedBrowsersDao
import fe.linksheet.module.database.entity.WhitelistedInAppBrowser
import fe.linksheet.module.database.entity.WhitelistedNormalBrowser
import kotlinx.coroutines.flow.Flow

@Dao
abstract class WhitelistedInAppBrowsersDao : WhitelistedBrowsersDao<WhitelistedInAppBrowser, WhitelistedInAppBrowser.Creator>(
    WhitelistedInAppBrowser.Creator
) {
    @Query("SELECT * FROM whitelisted_in_app_browser")
    abstract override fun getAll(): Flow<List<WhitelistedInAppBrowser>>

    @Query("DELETE FROM whitelisted_in_app_browser WHERE packageName = :packageName")
    abstract override suspend fun deleteByPackageName(packageName: String)
}
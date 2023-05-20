package fe.linksheet.module.database.dao

import androidx.room.Dao
import androidx.room.Query
import fe.linksheet.module.database.dao.base.WhitelistedBrowsersDao
import fe.linksheet.module.database.entity.WhitelistedNormalBrowser
import kotlinx.coroutines.flow.Flow

@Dao
abstract class WhitelistedNormalBrowsersDao : WhitelistedBrowsersDao<WhitelistedNormalBrowser, WhitelistedNormalBrowser.Creator>(
    WhitelistedNormalBrowser.Creator
) {
    @Query("SELECT * FROM whitelisted_browser")
    abstract override fun getAll(): Flow<List<WhitelistedNormalBrowser>>

    @Query("DELETE FROM whitelisted_browser WHERE packageName = :packageName")
    abstract override suspend fun deleteByPackageName(packageName: String)
}
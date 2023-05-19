package fe.linksheet.data.dao

import androidx.room.Dao
import androidx.room.Query
import fe.linksheet.data.dao.base.PackageEntityDao
import fe.linksheet.data.entity.WhitelistedBrowser
import kotlinx.coroutines.flow.Flow

@Dao
abstract class WhitelistedBrowsersDao : PackageEntityDao<WhitelistedBrowser, WhitelistedBrowser.Creator>(
    WhitelistedBrowser.Creator
) {
    @Query("SELECT * FROM whitelisted_browser")
    abstract fun getAll(): Flow<List<WhitelistedBrowser>>

    @Query("DELETE FROM whitelisted_browser WHERE packageName = :packageName")
    abstract override suspend fun deleteByPackageName(packageName: String)
}
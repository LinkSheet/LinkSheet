package fe.linksheet.module.database.dao.whitelisted

import androidx.room3.Dao
import androidx.room3.Query
import app.linksheet.api.database.BaseDao
import fe.linksheet.module.database.entity.whitelisted.WhitelistedNormalBrowser
import kotlinx.coroutines.flow.Flow

@Dao
interface WhitelistedNormalBrowsersDao : BaseDao<WhitelistedNormalBrowser> {
    @Query("SELECT * FROM whitelisted_browser")
    fun getAll(): Flow<List<WhitelistedNormalBrowser>>

    @Query("DELETE FROM whitelisted_browser WHERE packageName = :packageName")
    suspend fun deleteByPackageOrComponentName(packageName: String)
}

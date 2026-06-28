package fe.linksheet.module.database.dao.whitelisted

import androidx.room3.Dao
import androidx.room3.Query
import app.linksheet.api.database.BaseDao
import app.linksheet.api.database.UserDataDao
import fe.linksheet.module.database.entity.whitelisted.WhitelistedInAppBrowser
import kotlinx.coroutines.flow.Flow

@Dao
interface WhitelistedInAppBrowsersDao : BaseDao<WhitelistedInAppBrowser>, UserDataDao {
    @Query("SELECT * FROM ${WhitelistedInAppBrowser.TABLE_NAME}")
    override fun getAll(): Flow<List<WhitelistedInAppBrowser>>
    @Query("DELETE FROM ${WhitelistedInAppBrowser.TABLE_NAME}")
    override suspend fun deleteAll()
    @Query("DELETE FROM ${WhitelistedInAppBrowser.TABLE_NAME} WHERE packageName = :packageName")
    suspend fun deleteByPackageOrComponentName(packageName: String)
}

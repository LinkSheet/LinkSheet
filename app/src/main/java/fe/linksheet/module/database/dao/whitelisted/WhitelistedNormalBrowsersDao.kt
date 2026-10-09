package fe.linksheet.module.database.dao.whitelisted

import androidx.room3.Dao
import androidx.room3.Query
import app.linksheet.api.database.BaseDao
import app.linksheet.api.database.UserDataDao
import fe.linksheet.module.database.entity.whitelisted.WhitelistedNormalBrowser
import kotlinx.coroutines.flow.Flow

@Dao
interface WhitelistedNormalBrowsersDao : BaseDao<WhitelistedNormalBrowser>, UserDataDao {
    @Query("SELECT * FROM ${WhitelistedNormalBrowser.TABLE_NAME}")
    override fun getAll(): Flow<List<WhitelistedNormalBrowser>>
    @Query("DELETE FROM ${WhitelistedNormalBrowser.TABLE_NAME}")
    override suspend fun deleteAll()
    @Query("DELETE FROM ${WhitelistedNormalBrowser.TABLE_NAME} WHERE packageName = :packageName")
    suspend fun deleteByPackageOrComponentName(packageName: String)
}

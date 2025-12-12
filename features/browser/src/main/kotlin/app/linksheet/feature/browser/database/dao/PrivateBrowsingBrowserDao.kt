package app.linksheet.feature.browser.database.dao

import androidx.room.Dao
import androidx.room.Query
import app.linksheet.api.database.BaseDao
import app.linksheet.feature.browser.database.entity.PrivateBrowsingBrowser
import kotlinx.coroutines.flow.Flow

@Dao
interface PrivateBrowsingBrowserDao : BaseDao<PrivateBrowsingBrowser> {
    @Query("SELECT * FROM private_browsing_browser")
    fun getAll(): Flow<List<PrivateBrowsingBrowser>>

    @Query("DELETE FROM private_browsing_browser WHERE flatComponentName = :flatComponentName")
    suspend fun deleteByFlatComponentName(flatComponentName: String)
}

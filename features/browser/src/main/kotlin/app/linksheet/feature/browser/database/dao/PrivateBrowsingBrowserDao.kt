package app.linksheet.feature.browser.database.dao

import androidx.room3.Dao
import androidx.room3.Query
import app.linksheet.api.database.BaseDao
import app.linksheet.api.database.UserDataDao
import app.linksheet.feature.browser.database.entity.PrivateBrowsingBrowser
import kotlinx.coroutines.flow.Flow

@Dao
interface PrivateBrowsingBrowserDao : BaseDao<PrivateBrowsingBrowser>, UserDataDao {
    @Query("SELECT * FROM ${PrivateBrowsingBrowser.TABLE_NAME}")
    override fun getAll(): Flow<List<PrivateBrowsingBrowser>>
    @Query("DELETE FROM ${PrivateBrowsingBrowser.TABLE_NAME}")
    override suspend fun deleteAll()
    @Query("SELECT * FROM ${PrivateBrowsingBrowser.TABLE_NAME} WHERE ${PrivateBrowsingBrowser.COLUMN_FLAT_COMPONENT_NAME} = :flatComponentName")
    suspend fun getByFlatComponentName(flatComponentName: String): PrivateBrowsingBrowser?

    @Query("DELETE FROM ${PrivateBrowsingBrowser.TABLE_NAME} WHERE ${PrivateBrowsingBrowser.COLUMN_FLAT_COMPONENT_NAME} = :flatComponentName")
    suspend fun deleteByFlatComponentName(flatComponentName: String)
}

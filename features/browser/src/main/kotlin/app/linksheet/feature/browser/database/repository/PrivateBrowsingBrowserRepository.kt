package app.linksheet.feature.browser.database.repository

import app.linksheet.feature.browser.database.dao.PrivateBrowsingBrowserDao
import app.linksheet.feature.browser.database.entity.PrivateBrowsingBrowser
import kotlinx.coroutines.flow.Flow

class PrivateBrowsingBrowserRepository internal constructor(private val dao: PrivateBrowsingBrowserDao) {
    fun getAll(): Flow<List<PrivateBrowsingBrowser>> {
        return dao.getAll()
    }

    suspend fun insert(flatComponentName: String) {
        dao.insert(PrivateBrowsingBrowser(flatComponentName = flatComponentName))
    }

    suspend fun deleteByFlatComponentName(flatComponentName: String) {
        dao.deleteByFlatComponentName(flatComponentName)
    }

    suspend fun delete(privateBrowsingBrowser: PrivateBrowsingBrowser) {
        dao.delete(privateBrowsingBrowser)
    }
}

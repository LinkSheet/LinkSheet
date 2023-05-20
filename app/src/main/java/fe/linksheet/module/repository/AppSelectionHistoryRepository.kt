package fe.linksheet.module.repository

import android.net.Uri
import fe.linksheet.data.dao.AppSelectionHistoryDao
import fe.linksheet.data.entity.AppSelectionHistory


class AppSelectionHistoryRepository(private val dao: AppSelectionHistoryDao) {
    suspend fun insert(appSelectionHistory: AppSelectionHistory) = dao.insert(appSelectionHistory)

    fun resolveHostHistory(uri: Uri?) = mutableMapOf<String, AppSelectionHistory>().apply {
        if (uri?.host != null) {
            dao.getByHost(uri.host!!).forEach { app ->
                if (app.lastUsed > this.getOrPut(app.packageName) { app }.lastUsed) {
                    this[app.packageName] = app
                }
            }
        }
    }
}

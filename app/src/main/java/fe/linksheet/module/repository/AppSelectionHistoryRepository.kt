package fe.linksheet.module.repository

import android.net.Uri
import fe.linksheet.module.database.dao.AppSelectionHistoryDao
import fe.linksheet.module.database.entity.AppSelectionHistory
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map


class AppSelectionHistoryRepository(private val dao: AppSelectionHistoryDao) {
    suspend fun insert(appSelectionHistory: AppSelectionHistory) = dao.insert(appSelectionHistory)
    suspend fun insert(appSelectionHistories: List<AppSelectionHistory>) = dao.insert(appSelectionHistories)

    suspend fun getLastUsedForHostGroupedByPackage(uri: Uri?): Map<String, Long>? {
        val host = uri?.host
        if (host != null) {
            val flow = dao.getLastUsedForHostGroupedByPackage(host)
            val appSelections = flow.firstOrNull()
            if (appSelections != null) {
                return appSelections.associate { it.packageName to it.maxLastUsed }
            }
        }


        return null
    }
}

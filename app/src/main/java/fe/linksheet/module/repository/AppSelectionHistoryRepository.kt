package fe.linksheet.module.repository

import android.net.Uri
import fe.linksheet.module.database.dao.AppSelectionHistoryDao
import fe.linksheet.module.database.entity.AppSelectionHistory
import kotlinx.coroutines.flow.firstOrNull


class AppSelectionHistoryRepository(private val dao: AppSelectionHistoryDao) {
    suspend fun insert(appSelectionHistory: AppSelectionHistory) = dao.insert(appSelectionHistory)
    suspend fun insert(appSelectionHistories: List<AppSelectionHistory>) = dao.insert(appSelectionHistories)

    suspend fun getLastUsedForHostGroupedByPackage(uri: Uri?): Map<String, Long>? {
        val host = uri?.host ?: return null
        val appSelections = dao.getLastUsedForHostGroupedByPackage(host).firstOrNull() ?: return null

        return appSelections.associate { it.packageName to it.maxLastUsed }
    }

    suspend fun delete(packageNames: List<String>) {
        dao.deleteByPackageNames(packageNames)
    }
}

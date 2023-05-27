package fe.linksheet.module.repository

import android.net.Uri
import fe.linksheet.module.database.dao.AppSelectionHistoryDao
import fe.linksheet.module.database.entity.AppSelectionHistory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map


class AppSelectionHistoryRepository(private val dao: AppSelectionHistoryDao) {
    suspend fun insert(appSelectionHistory: AppSelectionHistory) = dao.insert(appSelectionHistory)

    suspend fun getLastUsedForHostGroupedByPackage(uri: Uri?) = uri?.host?.let { host ->
        dao.getLastUsedForHostGroupedByPackage(host).map { appSelections ->
            appSelections.associate { it.packageName to it.maxLastUsed }
        }.first()
    }
}

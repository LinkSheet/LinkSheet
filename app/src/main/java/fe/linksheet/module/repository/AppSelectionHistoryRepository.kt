package fe.linksheet.module.repository

import android.net.Uri
import fe.linksheet.extension.putOrUpdateIf
import fe.linksheet.extension.toMapWithPredicate
import fe.linksheet.module.database.dao.AppSelectionHistoryDao
import fe.linksheet.module.database.entity.AppSelectionHistory


class AppSelectionHistoryRepository(private val dao: AppSelectionHistoryDao) {
    suspend fun insert(appSelectionHistory: AppSelectionHistory) = dao.insert(appSelectionHistory)

    fun getHostHistory(uri: Uri?) = if (uri?.host != null) {
        dao.getByHost(uri.host!!).toMapWithPredicate(
            keySelector = { it.host },
            valueSelector = { it },
            predicate = { existing, new -> new.lastUsed > existing.lastUsed }
        )
    } else emptyMap()
}

package fe.linksheet.module.repository

import android.net.Uri
import fe.linksheet.extension.toMapWithPredicate
import fe.linksheet.module.database.dao.AppSelectionHistoryDao
import fe.linksheet.module.database.entity.AppSelectionHistory
import fe.linksheet.util.flowOfLazy
import kotlinx.coroutines.flow.map


class AppSelectionHistoryRepository(private val dao: AppSelectionHistoryDao) {
    suspend fun insert(appSelectionHistory: AppSelectionHistory) = dao.insert(appSelectionHistory)

    fun getHostHistory(uri: Uri?) = if (uri?.host != null) {
        dao.getByHost(uri.host!!).map { apps ->
            apps.toMapWithPredicate(
                keySelector = { it.host },
                valueSelector = { it },
                predicate = { existing, new -> new.lastUsed > existing.lastUsed }
            )
        }
    } else flowOfLazy { emptyMap() }
}

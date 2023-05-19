package fe.linksheet.module.repository

import fe.linksheet.data.dao.AppSelectionHistoryDao
import fe.linksheet.data.entity.AppSelectionHistory


class AppSelectionHistoryRepository(private val dao: AppSelectionHistoryDao) {
    suspend fun insert(appSelectionHistory: AppSelectionHistory) = dao.insert(appSelectionHistory)
}

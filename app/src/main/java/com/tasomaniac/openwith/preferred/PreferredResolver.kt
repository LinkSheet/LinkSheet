package com.tasomaniac.openwith.preferred

import android.content.Context
import com.tasomaniac.openwith.data.LinkSheetDatabase
import fe.linksheet.data.entity.AppSelectionHistory
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

object PreferredResolver : KoinComponent {
    private val database by inject<LinkSheetDatabase>()

    suspend fun resolve(context: Context, host: String) = database.preferredAppDao().getByHost(host)
        ?.toPreferredDisplayActivityInfo(context)

    fun resolveHostHistory(host: String): Map<String, AppSelectionHistory> {
        val map = mutableMapOf<String, AppSelectionHistory>()
        database.appSelectionHistoryDao().getByHost(host).forEach { app ->
            if (app.lastUsed > map.getOrPut(app.packageName) { app }.lastUsed) {
                map[app.packageName] = app
            }
        }

        return map
    }
}

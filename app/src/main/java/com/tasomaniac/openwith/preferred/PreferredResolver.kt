package com.tasomaniac.openwith.preferred

import android.content.Context
import com.tasomaniac.openwith.data.LinkSheetDatabase
import fe.linksheet.data.entity.AppSelectionHistory
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

object PreferredResolver : KoinComponent {
    private val database by inject<LinkSheetDatabase>()

    suspend fun resolve(context: Context, host: String) = database.preferredAppDao().getByHost(host).first()
        ?.toPreferredDisplayActivityInfo(context)
}

package fe.linksheet.module.viewmodel

import android.app.Application
import android.content.Context
import fe.android.preference.helper.PreferenceRepository
import fe.android.preference.helper.compose.getState
import fe.linksheet.LinkSheetApp
import fe.linksheet.module.preference.Preferences
import fe.linksheet.module.viewmodel.base.BaseViewModel
import fe.linksheet.util.flowOfLazy

class LogSettingsViewModel(
    context: Application,
    val preferenceRepository: PreferenceRepository
) : BaseViewModel(preferenceRepository) {
    val files = flowOfLazy {
        context.getDir(LinkSheetApp.logDir, Context.MODE_PRIVATE).listFiles()
            ?.filter { it.length() > 0L }
            ?.sortedDescending()?.toList() ?: emptyList()
    }
}
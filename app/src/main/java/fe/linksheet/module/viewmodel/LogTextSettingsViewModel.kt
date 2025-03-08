package fe.linksheet.module.viewmodel

import android.app.Application
import android.content.ClipboardManager
import androidx.core.content.getSystemService
import fe.linksheet.module.log.file.LogPersistService
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.viewmodel.base.BaseViewModel
import fe.linksheet.module.viewmodel.util.LogViewCommon
import fe.linksheet.util.flowOfLazy
import kotlinx.coroutines.flow.map

class LogTextSettingsViewModel(
    context: Application,
    private val sessionId: String?,
    val logViewCommon: LogViewCommon,
    val preferenceRepository: AppPreferenceRepository,
    private val logPersistService: LogPersistService,
) : BaseViewModel(preferenceRepository) {

    val clipboardManager = context.getSystemService<ClipboardManager>()!!

    val logEntries = flowOfLazy { sessionId }.map { logPersistService.readEntries(it) }
}

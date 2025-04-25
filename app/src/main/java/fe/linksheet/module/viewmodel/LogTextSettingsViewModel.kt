package fe.linksheet.module.viewmodel

import android.app.Application
import android.content.ClipboardManager
import androidx.core.content.getSystemService
import fe.linksheet.composable.page.settings.debug.log.PrefixMessageCardContent
import fe.linksheet.module.log.file.LogPersistService
import fe.linksheet.module.log.file.entry.LogEntry
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
    val merger = LogEntryMerger()

    val clipboardManager = context.getSystemService<ClipboardManager>()!!

    val logEntries = flowOfLazy { sessionId }.map { logPersistService.readEntries(it) }

}

class LogEntryMerger() {
    fun mergeEntries(logEntries: List<LogEntry>): List<PrefixMessageCardContent> {
        val merged = mutableListOf<PrefixMessageCardContent>()
        var last: PrefixMessageCardContent? = null
        for (entry in logEntries) {
            if (last != null) {
                if (last.isSamePrefix(entry)) {
                    last.add(entry)
                } else {
                    merged.add(last)
                    last = null
                }
            }

            if (last == null) {
                last = PrefixMessageCardContent(entry.type, entry.prefix, entry.unixMillis)
                last.add(entry)
            }
        }

        if (last != null) {
            merged.add(last)
        }

        return merged
    }
}

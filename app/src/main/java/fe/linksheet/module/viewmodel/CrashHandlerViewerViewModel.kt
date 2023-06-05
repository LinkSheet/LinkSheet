package fe.linksheet.module.viewmodel

import android.content.ClipboardManager
import android.content.Context
import androidx.core.content.getSystemService
import fe.android.preference.helper.PreferenceRepository
import fe.linksheet.module.log.LoggerFactory
import fe.linksheet.module.viewmodel.base.BaseViewModel
import fe.linksheet.module.viewmodel.util.LogViewCommon

class CrashHandlerViewerViewModel(
    context: Context,
    loggerFactory: LoggerFactory,
    val preferenceRepository: PreferenceRepository
) : BaseViewModel(preferenceRepository) {
    val clipboardManager = context.getSystemService<ClipboardManager>()!!
    val logViewCommon = LogViewCommon(preferenceRepository, loggerFactory)
}
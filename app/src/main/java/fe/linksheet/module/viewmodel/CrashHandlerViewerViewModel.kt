package fe.linksheet.module.viewmodel

import android.content.ClipboardManager
import android.content.Context
import androidx.core.content.getSystemService
import fe.linksheet.module.preference.AppPreferenceRepository

import fe.linksheet.module.log.LoggerFactory
import fe.linksheet.module.viewmodel.base.BaseViewModel
import fe.linksheet.module.viewmodel.util.LogViewCommon

class CrashHandlerViewerViewModel(
    context: Context,
    loggerFactory: LoggerFactory,
    val preferenceRepository: AppPreferenceRepository
) : BaseViewModel(preferenceRepository) {
    val clipboardManager = context.getSystemService<ClipboardManager>()!!
    val logViewCommon = LogViewCommon(preferenceRepository, loggerFactory.createLogger(CrashHandlerViewerViewModel::class))
}
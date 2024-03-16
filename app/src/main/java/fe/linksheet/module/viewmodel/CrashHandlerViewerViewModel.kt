package fe.linksheet.module.viewmodel

import android.content.ClipboardManager
import android.content.Context
import androidx.core.content.getSystemService
import com.google.gson.Gson
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.experiment.ExperimentRepository

import fe.linksheet.module.redactor.Redactor
import fe.linksheet.module.viewmodel.base.BaseViewModel
import fe.linksheet.module.viewmodel.util.LogViewCommon

class CrashHandlerViewerViewModel(
    context: Context,
    redactor: Redactor,
    gson: Gson,
    val preferenceRepository: AppPreferenceRepository,
    experimentRepository: ExperimentRepository,
) : BaseViewModel(preferenceRepository) {
    val clipboardManager = context.getSystemService<ClipboardManager>()!!
    val logViewCommon = LogViewCommon(preferenceRepository, experimentRepository, gson, redactor)
}

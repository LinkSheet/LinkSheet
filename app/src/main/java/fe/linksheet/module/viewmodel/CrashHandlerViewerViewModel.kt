package fe.linksheet.module.viewmodel

import android.content.ClipboardManager
import android.content.Context
import androidx.core.content.getSystemService
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.experiment.ExperimentRepository
import fe.linksheet.module.preference.experiment.Experiments
import fe.linksheet.module.viewmodel.base.BaseViewModel
import fe.linksheet.module.viewmodel.util.LogViewCommon

class CrashHandlerViewerViewModel(
    context: Context,
    val preferenceRepository: AppPreferenceRepository,
    experimentsRepository: ExperimentRepository,
    val logViewCommon: LogViewCommon,
) : BaseViewModel(preferenceRepository) {
    val clipboardManager = context.getSystemService<ClipboardManager>()!!

    val uiOverhaul = experimentsRepository.asState(Experiments.uiOverhaul)
}

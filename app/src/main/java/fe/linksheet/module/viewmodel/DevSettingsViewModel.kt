package fe.linksheet.module.viewmodel


import android.app.Application
import android.widget.Toast
import fe.linksheet.R
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.app.AppPreferences
import fe.linksheet.module.preference.experiment.ExperimentRepository
import fe.linksheet.module.preference.experiment.Experiments
import fe.linksheet.module.shizuku.ShizukuCommand
import fe.linksheet.module.shizuku.ShizukuHandler
import fe.linksheet.module.viewmodel.base.BaseViewModel

class DevSettingsViewModel(
    val context: Application,
    preferenceRepository: AppPreferenceRepository,
    experimentRepository: ExperimentRepository,
    private val shizukuHandler: ShizukuHandler,
) : BaseViewModel(preferenceRepository) {
    var devModeEnabled = preferenceRepository.asState(AppPreferences.devModeEnabled)
    val useDevBottomSheet = preferenceRepository.asState(AppPreferences.useDevBottomSheet)
    val devBottomSheetExperiment = preferenceRepository.asState(AppPreferences.devBottomSheetExperiment)

    val enableAnalytics = experimentRepository.asState(Experiments.enableAnalytics)

    fun enqueueResetAppLinks() {
        val command = ShizukuCommand(command = { reset("all") }, resultHandler = {
            Toast.makeText(context, context.getText(R.string.reset_app_link_verification_status_toast), Toast.LENGTH_SHORT).show()
        })

        shizukuHandler.enqueueCommand(command)
    }
}

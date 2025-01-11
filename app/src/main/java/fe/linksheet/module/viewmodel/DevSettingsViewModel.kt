package fe.linksheet.module.viewmodel


import android.app.Application
import android.widget.Toast
import com.google.gson.Gson
import fe.linksheet.R
import fe.linksheet.module.systeminfo.SystemInfoService
import fe.linksheet.module.devicecompat.miui.MiuiCompatProvider
import fe.linksheet.module.devicecompat.miui.MiuiAuditor
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.experiment.ExperimentRepository
import fe.linksheet.module.shizuku.ShizukuCommand
import fe.linksheet.module.shizuku.ShizukuHandler
import fe.linksheet.module.viewmodel.base.BaseViewModel

class DevSettingsViewModel(
    val context: Application,
    preferenceRepository: AppPreferenceRepository,
    experimentRepository: ExperimentRepository,
    private val shizukuHandler: ShizukuHandler,
    miuiCompatProvider: MiuiCompatProvider,
    val gson: Gson,
    val systemInfoService: SystemInfoService,
) : BaseViewModel(preferenceRepository) {
    private val auditor = MiuiAuditor(systemInfoService)

    val miuiCompatRequired by miuiCompatProvider.isRequired

    fun enqueueResetAppLinks() {
        val command = ShizukuCommand(command = { reset("all") }, resultHandler = {
            Toast.makeText(
                context,
                context.getText(R.string.reset_app_link_verification_status_toast),
                Toast.LENGTH_SHORT
            ).show()
        })

        shizukuHandler.enqueueCommand(command)
    }

    fun auditMiuiEnvironment(): String {
        val audit = auditor.audit(context)
        return gson.toJson(audit)
    }
}

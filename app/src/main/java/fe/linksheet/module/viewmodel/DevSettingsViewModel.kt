package fe.linksheet.module.viewmodel


import android.app.Application
import android.widget.Toast
import com.google.gson.Gson
import fe.gson.dsl.jsonObject
import fe.linksheet.R
import fe.linksheet.module.devicecompat.MiuiCompatProvider
import fe.linksheet.util.device.xiaomi.MIUIAuditor
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
) : BaseViewModel(preferenceRepository) {

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
        val opStatus = MIUIAuditor.getMiuiOpStatus(context)
        val environment = jsonObject {
            for ((op, status) in opStatus) {
                "$op" += status
            }
        }

        return gson.toJson(environment)
    }
}

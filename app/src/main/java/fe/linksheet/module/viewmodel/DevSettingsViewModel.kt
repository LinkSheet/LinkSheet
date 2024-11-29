package fe.linksheet.module.viewmodel


import android.app.Application
import android.widget.Toast
import com.google.gson.Gson
import fe.gson.dsl.jsonObject
import fe.linksheet.R
import fe.linksheet.util.xiaomi.MIUIAuditor
import fe.linksheet.module.preference.app.AppPreferenceRepository
import fe.linksheet.module.preference.experiment.ExperimentRepository
import fe.linksheet.module.shizuku.ShizukuCommand
import fe.linksheet.module.shizuku.ShizukuHandler
import fe.linksheet.module.viewmodel.base.BaseViewModel
import fe.linksheet.util.BuildInfo

class DevSettingsViewModel(
    val context: Application,
    preferenceRepository: AppPreferenceRepository,
    experimentRepository: ExperimentRepository,
    private val shizukuHandler: ShizukuHandler,
    val gson: Gson,
) : BaseViewModel(preferenceRepository) {

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
        val properties = MIUIAuditor.getProperties()
        val opStatus = MIUIAuditor.getMiuiOpStatus(context)
        val intentExists = MIUIAuditor.checkIntentExists(context)

        val environment = jsonObject {
            "properties" += properties
            "opStatus" += opStatus
            "intentExists" += intentExists
        }

        return gson.toJson(environment)
    }
}

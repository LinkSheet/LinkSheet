package fe.linksheet.module.devicecompat

import android.app.Activity
import android.app.AppOpsManager
import android.app.AppOpsManagerHidden
import android.content.Context
import android.os.Build
import android.os.Process
import android.os.SystemProperties
import androidx.core.content.getSystemService
import dev.rikka.tools.refine.Refine
import fe.linksheet.module.intent.buildIntent
import org.koin.dsl.module


val MiuiCompatModule = module {
    single<MiuiCompat> {
        if (MiuiCompat.isRequired) {
            val context = get<Context>()
            val appOpsManager = context.getSystemService<AppOpsManager>()!!
            val appOpsManagerHidden = Refine.unsafeCast<AppOpsManagerHidden>(appOpsManager)

            RealMiuiCompat(context, appOpsManager, appOpsManagerHidden)
        } else {
            NoOpMiuiCompat
        }
    }
}

interface MiuiCompat {
    companion object {
        private val isXiaomiDevice = Build.MANUFACTURER.contains("xiaomi", ignoreCase = true)
        private val miuiVersion by lazy { readMiuiVersion() }

        val isRequired by lazy {
            isXiaomiDevice && miuiVersion != null
        }

        private fun readMiuiVersion(): Int? {
            val result = runCatching { SystemProperties.get("ro.miui.ui.version.code") }
            if (result.isFailure) {
                return null
            }

            val version = result.getOrNull()
            if (version.isNullOrEmpty()) return null
            return version.toIntOrNull()
        }
    }

    fun showAlert(context: Context): Boolean
    fun startPermissionRequest(activity: Activity): Boolean
}

object NoOpMiuiCompat : MiuiCompat {
    override fun showAlert(context: Context) = false
    override fun startPermissionRequest(activity: Activity) = true
}

class RealMiuiCompat(
    private val context: Context,
    private val appOpsManager: AppOpsManager,
    private val appOpsManagerHidden: AppOpsManagerHidden,
) : MiuiCompat {

    enum class Mode {
        Default, Rejected, Prompt, Accepted
    }

    override fun showAlert(context: Context): Boolean {
        return getAutoStartMode(context) != Mode.Accepted
    }

    fun getAutoStartMode(context: Context): Mode? {
        return runCatching { appOpsManagerHidden.checkOp(10021, Process.myUid(), context.packageName) }
            .map { Mode.entries[it] }
            .getOrNull()
    }

    override fun startPermissionRequest(activity: Activity): Boolean {
        val intent = buildIntent("miui.intent.action.APP_PERM_EDITOR") {
            putExtra("extra_package_uid", Process.myUid())
            putExtra("extra_pkgname", context.packageName)
            putExtra("extra_package_name", context.packageName)
        }

        return runCatching { activity.startActivity(intent) }.map { true }.getOrDefault(false)
    }
}

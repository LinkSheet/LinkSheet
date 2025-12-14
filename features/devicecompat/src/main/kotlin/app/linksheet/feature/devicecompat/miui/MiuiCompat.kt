package app.linksheet.feature.devicecompat.miui

import android.app.Activity
import android.app.AppOpsManager
import android.content.Context
import android.os.Process
import androidx.core.content.getSystemService
import app.linksheet.api.RefineWrapper
import app.linksheet.api.WrappedAppOpsManagerHidden
import fe.composekit.intent.buildIntent
import fe.linksheet.feature.systeminfo.SystemInfoService
import fe.std.lazy.ResettableLazy
import fe.std.lazy.resettableLazy

interface MiuiCompatProvider {
    val isXiaomiDevice: Boolean
    fun readMiuiVersion(): Int?

    val isRequired: ResettableLazy<Boolean>

    fun provideCompat(context: Context): MiuiCompat
}

class RealMiuiCompatProvider(
    private val infoService: SystemInfoService,
    private val refineWrapper: RefineWrapper,
) : MiuiCompatProvider {
    override val isXiaomiDevice = infoService.build.manufacturer.contains("xiaomi", ignoreCase = true)

    override fun readMiuiVersion(): Int? {
        val result = runCatching { infoService.properties.get("ro.miui.ui.version.code") }
        return result.map { it?.toIntOrNull() }.getOrNull()
    }

    override val isRequired = resettableLazy {
        isXiaomiDevice && !infoService.isCustomRom && readMiuiVersion() != null
    }

    override fun provideCompat(context: Context): MiuiCompat {
        if (!isRequired.value) return NoOpMiuiCompat

        val appOpsManager = context.getSystemService<AppOpsManager>()!!
        return RealMiuiCompat(refineWrapper.cast(appOpsManager))
    }
}

interface MiuiCompat {
    fun showAlert(context: Context): Boolean
    fun startPermissionRequest(activity: Activity): Boolean
}

object NoOpMiuiCompat : MiuiCompat {
    override fun showAlert(context: Context) = false
    override fun startPermissionRequest(activity: Activity) = true
}

class RealMiuiCompat(
    private val appOpsManager: WrappedAppOpsManagerHidden,
) : MiuiCompat {
    companion object {
        private const val OP_BACKGROUND_START_ACTIVITY = 10021
    }

    override fun showAlert(context: Context): Boolean {
        return !hasBackgroundStartPermission(context)
    }

    private fun hasBackgroundStartPermission(context: Context): Boolean {
        return runCatching { appOpsManager.checkOp(OP_BACKGROUND_START_ACTIVITY, Process.myUid(), context.packageName) }
            .map { it == 0 }
            .getOrDefault(false)
    }

    override fun startPermissionRequest(activity: Activity): Boolean {
        val intent = buildIntent("miui.intent.action.APP_PERM_EDITOR") {
            putExtra("extra_package_uid", Process.myUid())
            putExtra("extra_pkgname", activity.packageName)
            putExtra("extra_package_name", activity.packageName)
        }

        return runCatching { activity.startActivity(intent) }.map { true }.getOrDefault(false)
    }
}

package fe.linksheet.module.devicecompat.miui

import android.app.Activity
import android.app.AppOpsManager
import android.app.AppOpsManagerHidden
import android.content.Context
import android.os.Process
import androidx.core.content.getSystemService
import dev.rikka.tools.refine.Refine
import fe.linksheet.module.systeminfo.SystemInfoService
import fe.linksheet.util.intent.buildIntent
import fe.std.lazy.ResettableLazy
import fe.std.lazy.resettableLazy

interface MiuiCompatProvider {
    val isXiaomiDevice: Boolean
    fun readMiuiVersion(): Int?

    val isRequired: ResettableLazy<Boolean>

    fun provideCompat(context: Context): MiuiCompat
}

class RealMiuiCompatProvider(
    val infoService: SystemInfoService,
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
        return RealMiuiCompat(appOpsManager)
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
    private val appOpsManager: AppOpsManager,
) : MiuiCompat {
    companion object {
        const val OP_BACKGROUND_START_ACTIVITY = 10021
    }

    private val hiddenAppOps by lazy {
        Refine.unsafeCast<AppOpsManagerHidden>(appOpsManager)
    }

    override fun showAlert(context: Context): Boolean {
        return !hasBackgroundStartPermission(context)
    }

    fun hasBackgroundStartPermission(context: Context): Boolean {
        return runCatching { hiddenAppOps.checkOp(OP_BACKGROUND_START_ACTIVITY, Process.myUid(), context.packageName) }
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

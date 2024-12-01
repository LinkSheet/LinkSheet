package fe.linksheet.module.devicecompat

import android.annotation.SuppressLint
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
import fe.linksheet.util.device.xiaomi.MIUIAuditor
import org.koin.dsl.module
import java.lang.reflect.Method


val MiuiCompatModule = module {
    single<MiuiCompat> {
        if (RealMiuiCompat.isXiaomiDevice) {
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

    companion object {
        val isXiaomiDevice = Build.MANUFACTURER.contains("xiaomi", ignoreCase = true)
    }

    fun getMiuiVersion(): Int? {
        if (!MIUIAuditor.isXiaomiDevice) return null

        val version = SystemProperties.get("ro.miui.ui.version.code")
        if (version.isNullOrEmpty()) return null

        return version.toIntOrNull()
    }

    private val appOpsUtil by lazy {
        @SuppressLint("PrivateApi")
        Class.forName("android.miui.AppOpsUtils")
    }

    private val getApplicationAutoStart: Method by lazy {
        appOpsUtil.getDeclaredMethod(
            "getApplicationAutoStart",
            Context::class.java,
            String::class.java
        )
    }

    private val setApplicationAutoStart: Method by lazy {
        appOpsUtil.getDeclaredMethod(
            "setApplicationAutoStart",
            Context::class.java,
            String::class.java,
            Boolean::class.java
        )
    }

    enum class Mode {
        Default, Rejected, Prompt, Accepted
    }

    override fun showAlert(context: Context): Boolean {
        if (!isXiaomiDevice) return false
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

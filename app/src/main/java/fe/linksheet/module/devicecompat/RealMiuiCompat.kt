package fe.linksheet.module.devicecompat

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.SystemProperties
import fe.linksheet.util.device.xiaomi.MIUIAuditor
import org.koin.dsl.module
import java.lang.reflect.Method


val MiuiCompatModule = module {
    single<MiuiCompat> {
        if (RealMiuiCompat.isXiaomiDevice) RealMiuiCompat() else NoOpMiuiCompat
    }
}

interface MiuiCompat {
    fun showAlert(context: Context): Boolean
    fun setAutoStart(context: Context, autoStart: Boolean): Boolean
}

object NoOpMiuiCompat : MiuiCompat {
    override fun showAlert(context: Context) = false
    override fun setAutoStart(context: Context, autoStart: Boolean) = true
}

class RealMiuiCompat : MiuiCompat {
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
        return runCatching { getApplicationAutoStart.invoke(null, context, context.packageName) as Int }
            .map { Mode.entries[it] }
            .getOrNull()
    }

    override fun setAutoStart(context: Context, autoStart: Boolean): Boolean {
        return runCatching { setApplicationAutoStart.invoke(null, context, context.packageName, autoStart) }
            .map { true }
            .getOrDefault(false)
    }
}

package fe.linksheet.module.devicecompat.miui

import android.app.AppOpsManager
import android.app.AppOpsManagerHidden
import android.content.Context
import android.os.Process
import androidx.annotation.Keep
import androidx.core.content.getSystemService
import dev.rikka.tools.refine.Refine
import fe.linksheet.module.systeminfo.BuildInfo
import fe.linksheet.module.systeminfo.SystemInfoService
import fe.linksheet.module.systeminfo.DeviceInfo

class MiuiAuditor(
    val service: SystemInfoService,
) {
    @Keep
    data class MiuiAudit(
        val buildInfo: BuildInfo,
        val deviceInfo: DeviceInfo,
        val fingerprint: String,
        val miui: MiuiVersion,
        val appOps: Map<Int, Int>,
        val properties: Map<String, String>,
    )

    @Keep
    data class MiuiVersion(
        val code: String?,
        val name: String?,
    )

    fun audit(context: Context): MiuiAudit {
        val code = service.properties.get("ro.miui.ui.version.code")
        val name = service.properties.get("ro.miui.ui.version.name")

        val properties = service.properties.getAllProperties()
            .filterKeys { it.startsWith("ro") }

        return MiuiAudit(
            service.buildInfo,
            service.deviceInfo,
            service.build.fingerprint,
            MiuiVersion(code, name),
            getMiuiOpStatus(context),
            properties
        )
    }

    private fun getMiuiOpStatus(context: Context): Map<Int, Int> {
        val appOpsManager = context.getSystemService<AppOpsManager>()!!
        val appOpsManagerHidden = Refine.unsafeCast<AppOpsManagerHidden>(appOpsManager)

        return (0..40).map { 10_000 + it }.associateWith {
            runCatching { appOpsManagerHidden.checkOp(it, Process.myUid(), context.packageName) }.getOrDefault(-1)
        }
    }
}

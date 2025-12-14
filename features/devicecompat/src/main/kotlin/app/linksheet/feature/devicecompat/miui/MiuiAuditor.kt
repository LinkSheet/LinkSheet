package app.linksheet.feature.devicecompat.miui

import android.app.AppOpsManager
import android.content.Context
import android.os.Process
import androidx.annotation.Keep
import androidx.core.content.getSystemService
import app.linksheet.api.RefineWrapper
import fe.linksheet.feature.systeminfo.BuildInfo
import fe.linksheet.feature.systeminfo.DeviceInfo
import fe.linksheet.feature.systeminfo.SystemInfoService

class MiuiAuditor(
    private val service: SystemInfoService,
    private val refineWrapper: RefineWrapper
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
            buildInfo = service.buildInfo,
            deviceInfo = service.deviceInfo,
            fingerprint = service.build.fingerprint,
            miui = MiuiVersion(code, name),
            appOps = getMiuiOpStatus(context),
            properties = properties
        )
    }

    private fun getMiuiOpStatus(context: Context): Map<Int, Int> {
        val appOpsManager = context.getSystemService<AppOpsManager>()!!
        val appOpsManagerHidden = refineWrapper.cast(appOpsManager)

        return (0..40).map { 10_000 + it }.associateWith {
            runCatching {
                appOpsManagerHidden.checkOp(it, Process.myUid(), context.packageName)
            }.getOrDefault(-1)
        }
    }
}

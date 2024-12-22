package fe.linksheet.util.device.xiaomi

import android.app.AppOpsManager
import android.app.AppOpsManagerHidden
import android.content.Context
import android.os.Process
import androidx.annotation.Keep
import androidx.core.content.getSystemService
import dev.rikka.tools.refine.Refine
import fe.kotlin.extension.string.substringOrNull
import fe.linksheet.module.build.BuildInfo
import fe.linksheet.module.build.BuildInfoService
import fe.linksheet.module.build.DeviceInfo
import fe.std.process.android.AndroidStartConfig
import fe.std.process.launchProcess


class MiuiAuditor(
    val infoService: BuildInfoService,
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
        val code = infoService.properties.get("ro.miui.ui.version.code")
        val name = infoService.properties.get("ro.miui.ui.version.name")

        val properties = getProperties()
            .filterKeys { it.startsWith("ro") }

        return MiuiAudit(
            infoService.buildInfo,
            infoService.deviceInfo,
            infoService.buildConstants.fingerprint,
            MiuiVersion(code, name),
            getMiuiOpStatus(context),
            properties
        )
    }

    private fun getProperties(): Map<String, String> {
        fun String.unwrap(): String? {
            return trim().run { substringOrNull(1, length - 1) }
        }

        fun String.parseLine(): Pair<String, String>? {
            val (wrappedKey, wrappedValue) = split(":")

            val key = wrappedKey.unwrap() ?: return null
            val value = wrappedValue.unwrap() ?: return null

            return key to value
        }

        return buildMap<String, String> {
            launchProcess("getprop", invokeOnEmpty = false, config = AndroidStartConfig) {
                it.parseLine()?.let { (key, value) -> put(key, value) }
            }
        }
    }

    private fun getMiuiOpStatus(context: Context): Map<Int, Int> {
        val appOpsManager = context.getSystemService<AppOpsManager>()!!
        val appOpsManagerHidden = Refine.unsafeCast<AppOpsManagerHidden>(appOpsManager)

        return (0..40).map { 10_000 + it }.associateWith {
            runCatching { appOpsManagerHidden.checkOp(it, Process.myUid(), context.packageName) }.getOrDefault(-1)
        }
    }
}

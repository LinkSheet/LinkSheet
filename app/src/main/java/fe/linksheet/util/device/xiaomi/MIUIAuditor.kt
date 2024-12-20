package fe.linksheet.util.device.xiaomi

import android.app.AppOpsManager
import android.app.AppOpsManagerHidden
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Process
import androidx.core.content.getSystemService
import dev.rikka.tools.refine.Refine
import fe.kotlin.extension.string.substringOrNull
import fe.linksheet.extension.android.queryIntentActivitiesCompat
import fe.linksheet.module.intent.buildIntent
import fe.std.process.android.AndroidStartConfig
import fe.std.process.launchProcess

object MIUIAuditor {
    fun getProperties(): Map<String, String> {
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

    fun getMiuiOpStatus(context: Context): Map<Int, Int> {
        val appOpsManager = context.getSystemService<AppOpsManager>()!!
        val appOpsManagerHidden = Refine.unsafeCast<AppOpsManagerHidden>(appOpsManager)

        return (0..40).map { 10_000 + it }.associateWith {
            runCatching { appOpsManagerHidden.checkOp(it, Process.myUid(), context.packageName) }.getOrDefault(-1)
        }
    }

    fun checkIntentExists(context: Context): Boolean {
        val intent = buildIntent("miui.intent.action.APP_PERM_EDITOR") {
            setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity")
            putExtra("extra_pkgname", context.packageName)
            setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        return context.packageManager.queryIntentActivitiesCompat(intent, PackageManager.MATCH_DEFAULT_ONLY).isNotEmpty()
    }
}

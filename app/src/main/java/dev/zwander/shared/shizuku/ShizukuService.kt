package dev.zwander.shared.shizuku

import android.content.Context
import android.os.UserHandle
import android.util.Log
import androidx.annotation.Keep
import dev.zwander.shared.IShizukuService
import fe.android.version.AndroidVersion
import fe.std.process.android.AndroidStartConfig
import fe.std.process.launchProcess
import kotlin.system.exitProcess

class ShizukuService : IShizukuService.Stub {
    @Keep
    constructor() : super()

    @Keep
    constructor(@Suppress("UNUSED_PARAMETER") context: Context) : super()

    override fun setDomainState(packageName: String, domains: String, enabled: Boolean): Int {
        if (AndroidVersion.isAtLeastApi31S()) {
            try {
                val userId = getUserId()
                return launchProcess(
                    "pm", "set-app-links-allowed",
                    "--user", userId,
                    "--package", packageName,
                    enabled.toString(),
                    config = AndroidStartConfig
                )
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }

        return -1
    }

    override fun reset(packageName: String) {
        val userId = getUserId()
        val setAppLinkAllowedResult = launchProcess(
            "pm", "set-app-links-allowed",
            "--user",
            userId,
            "--package", packageName, "true",
            config = AndroidStartConfig
        ) { line ->
            Log.d("ShizukuService", line)
        }

        val resetAppLinkResult = launchProcess(
            "pm",
            "reset-app-links",
            "--user",
            userId,
            packageName,
            config = AndroidStartConfig
        ) { line ->
            Log.d("ShizukuService", line)
        }

        val verifyAppLinksResult = launchProcess(
            "pm", "verify-app-links", "--re-verify", packageName,
            config = AndroidStartConfig
        ) { line ->
            Log.d("ShizukuService", line)
        }
    }

    private fun getUserId(): String {
        return UserHandle::class.java.getMethod("myUserId").invoke(null)!!.toString()
    }

    override fun destroy() {
        exitProcess(0)
    }
}

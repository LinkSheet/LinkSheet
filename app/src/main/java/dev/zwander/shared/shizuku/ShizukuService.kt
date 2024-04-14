package dev.zwander.shared.shizuku

import android.content.Context
import android.os.UserHandle
import android.util.Log
import androidx.annotation.Keep
import dev.zwander.shared.IShizukuService
import fe.linksheet.util.AndroidVersion
import fe.processlauncher.launchProcess
import kotlin.system.exitProcess

class ShizukuService : IShizukuService.Stub {
    @Keep
    constructor() : super()

    @Keep
    constructor(@Suppress("UNUSED_PARAMETER") context: Context) : super()

    override fun setDomainState(packageName: String, domains: String, enabled: Boolean): Int {
        if (AndroidVersion.AT_LEAST_API_31_S) {
            try {
                val userId = getUserId()
                return launchProcess(
                    "pm", "set-app-links-allowed",
                    "--user", userId,
                    "--package", packageName,
                    enabled.toString()
                ).waitFor()
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }

        return -1
    }

    override fun reset(packageName: String) {
        val setAppLinkAllowedResult = launchProcess(
            "pm", "set-app-links-allowed",
            "--user",
            getUserId(),
            "--package", packageName, "true",
        ) { line -> Log.d("ShizukuService", line) }

        val setAppLinkStateResult = launchProcess(
            "pm", "set-app-links",
            "--package", packageName,
            "0",
            "all"
        ) { line -> Log.d("ShizukuService", line) }

        val verifyAppLinksResult = launchProcess(
            "pm", "verify-app-links", "--re-verify", packageName
        ) { line -> Log.d("ShizukuService", line) }
    }

    private fun getUserId(): String {
        return UserHandle::class.java.getMethod("myUserId").invoke(null)!!.toString()
    }

    override fun destroy() {
        exitProcess(0)
    }
}

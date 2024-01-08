package dev.zwander.shared.shizuku

import android.content.Context
import android.os.UserHandle
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

    override fun disableLinkHandling(packageName: String, enabled: Boolean): Int {
        try {
            return if (AndroidVersion.AT_LEAST_API_31_S) {
                launchProcess(
                    "pm", "set-app-links-allowed",
                    "--user",
                    UserHandle::class.java
                        .getMethod("myUserId")
                        .invoke(null)!!.toString(),
                    "--package", packageName,
                    enabled.toString()
                ).waitFor()
            } else -1
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        return -1
    }

    override fun destroy() {
        exitProcess(0)
    }
}

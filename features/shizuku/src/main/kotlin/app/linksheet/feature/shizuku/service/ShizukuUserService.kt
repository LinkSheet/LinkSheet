package app.linksheet.feature.shizuku.service

import android.content.Context
import android.os.UserHandle
import android.util.Log
import androidx.annotation.Keep
import app.linksheet.feature.shizuku.IShizukuUserService
import fe.std.process.android.AndroidStartConfig
import fe.std.process.launchProcess
import kotlin.system.exitProcess

class ShizukuUserService : IShizukuUserService.Stub {
    @Keep
    constructor() : super()

    @Keep
    constructor(@Suppress("UNUSED_PARAMETER") context: Context) : super()

    override fun verify(packageName: String?): Int {
        val args = listOfNotNull("cmd", "package", "verify-app-links", packageName)
        Log.d("ShizukuUserService", "verify: args=$args")
        val code = launchProcess(
            args = args.toTypedArray(),
            config = AndroidStartConfig
        ) { line ->
            Log.d("ShizukuUserService", "verify: output=$line")
        }
        Log.d("ShizukuUserService", "verify: code=$code")
        return code
    }

    override fun reset(packageName: String?): Int {
        val code = launchProcess(
            "cmd",
            "package",
            "reset-app-links",
            packageName ?: "all",
            config = AndroidStartConfig
        ) { line ->
            Log.d("ShizukuUserService", "reset: output=$line")
        }
        Log.d("ShizukuUserService", "reset: code=$code")
        return code
    }

    private fun getUserId(): String {
        return UserHandle::class.java.getMethod("myUserId").invoke(null)!!.toString()
    }

    override fun destroy() {
        exitProcess(0)
    }
}

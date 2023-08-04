package dev.zwander.shared.shizuku

import android.util.Log
import dev.zwander.shared.IShizukuService
import dev.zwander.shared.data.VerifyResult
import fe.linksheet.util.AndroidVersion
import kotlin.system.exitProcess

class ShizukuService : IShizukuService.Stub() {

    override fun verifyLinks(sdk: Int, packageName: String): VerifyResult? {
        try {
            return if (AndroidVersion.AT_LEAST_API_31_S) {
                val output = ArrayList<String>()

                Log.d("Shizuku", "VerifyLinks")
//                val process = Runtime.getRuntime()
//                    .exec("cmd package set-app-links --package $packageName 2 all")
//                process.inputStream.bufferedReader().forEachLine {
//                    Log.d("Shizuku", "In: $it")
//                    output.add(it)
//                }
//                process.errorStream.bufferedReader().forEachLine {
//                    Log.d("Shizuku", "error: $it")
//                    output.add(it)
//                }
//
//                val result = process.waitFor()

                VerifyResult(
                    output = output,
                    result = 1,
                )
            } else {
                null
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        } finally {
//            LinkVerifyUtils.verifyAllLinks(packageName)
        }

        return null
    }

    override fun destroy() {
        exitProcess(0)
    }
}
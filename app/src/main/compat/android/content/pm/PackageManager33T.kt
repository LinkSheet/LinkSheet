package android.content.pm

import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import fe.linksheet.util.AndroidVersion

object FlagCache {
    private val resolveInfo = mutableMapOf<Int, PackageManager.ResolveInfoFlags>()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private inline fun <T> MutableMap<Int, T>.resolve(flag: Int, convert: (Int) -> T): T {
        val cached = this[flag]
        if (cached != null) return cached

        val resolveInfoFlag = convert(flag)
        this[flag] = resolveInfoFlag
        return resolveInfoFlag
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun toResolveInfoFlags(flags: Int): PackageManager.ResolveInfoFlags {
        return resolveInfo.resolve(flags) { PackageManager.ResolveInfoFlags.of(it.toLong()) }
    }
}

fun PackageManager.queryIntentActivitiesCompat(intent: Intent, flags: Int): MutableList<ResolveInfo> {
    return if (AndroidVersion.AT_LEAST_API_33_T) queryIntentActivities(intent, FlagCache.toResolveInfoFlags(flags))
    else queryIntentActivities(intent, flags)
}

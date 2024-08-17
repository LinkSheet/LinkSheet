package fe.linksheet.util

import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi


interface Flags {
    val value: Int

    fun contains(flag: Int) = (value and flag) != 0
}

@JvmInline
value class ResolveInfoFlags(override val value: Int) : Flags {
    companion object {
        val GET_META_DATA = ResolveInfoFlags(PackageManager.GET_META_DATA)
        val GET_RESOLVED_FILTER = ResolveInfoFlags(PackageManager.GET_RESOLVED_FILTER)
        val GET_SHARED_LIBRARY_FILES = ResolveInfoFlags(PackageManager.GET_SHARED_LIBRARY_FILES)
        val MATCH_ALL = ResolveInfoFlags(PackageManager.MATCH_ALL)

        val MATCH_DISABLED_COMPONENTS = ResolveInfoFlags(PackageManager.MATCH_DISABLED_COMPONENTS)
        val MATCH_DISABLED_UNTIL_USED_COMPONENTS = ResolveInfoFlags(PackageManager.MATCH_DISABLED_UNTIL_USED_COMPONENTS)
        val MATCH_DEFAULT_ONLY = ResolveInfoFlags(PackageManager.MATCH_DEFAULT_ONLY)

        @RequiresApi(Build.VERSION_CODES.Q)
        val MATCH_DIRECT_BOOT_AUTO = ResolveInfoFlags(PackageManager.MATCH_DIRECT_BOOT_AUTO)
        val MATCH_DIRECT_BOOT_AWARE = ResolveInfoFlags(PackageManager.MATCH_DIRECT_BOOT_AWARE)
        val MATCH_DIRECT_BOOT_UNAWARE = ResolveInfoFlags(PackageManager.MATCH_DIRECT_BOOT_UNAWARE)
        val MATCH_SYSTEM_ONLY = ResolveInfoFlags(PackageManager.MATCH_SYSTEM_ONLY)
        val MATCH_UNINSTALLED_PACKAGES = ResolveInfoFlags(PackageManager.MATCH_UNINSTALLED_PACKAGES)

        fun <T : Flags> select(vararg flags: T): ResolveInfoFlags {
            return ResolveInfoFlags(flags.sumOf { it.value })
        }
    }
}

package fe.linksheet.util

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi


interface Flags {
    val value: Int
    val inv: Int
        get() = value.inv()

    operator fun contains(flag: Int): Boolean {
        return (value and flag) != 0
    }

    fun Int.remove(): Int {
        return this.and(inv)
    }
}

interface FlagCompanion<T : Flags> {
    val new: (Int) -> T

    fun select(vararg flags: T): T {
        val sum = flags.sumOf { it.value }
        return new(sum)
    }

    fun T.inv(): T {
        return new(this@inv.inv)
    }
}

@JvmInline
value class ResolveInfoFlags(override val value: Int) : Flags {
    companion object : FlagCompanion<ResolveInfoFlags> {
        val EMPTY = ResolveInfoFlags(0)

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

        override val new: (Int) -> ResolveInfoFlags = {
            ResolveInfoFlags(it)
        }
    }
}

@JvmInline
value class ApplicationInfoFlags(override val value: Int) : Flags {
    companion object : FlagCompanion<ApplicationInfoFlags> {
        val EMPTY = ApplicationInfoFlags(0)

        val GET_META_DATA = ApplicationInfoFlags(PackageManager.GET_META_DATA)
        val GET_SHARED_LIBRARY_FILES = ApplicationInfoFlags(PackageManager.GET_SHARED_LIBRARY_FILES)
        val MATCH_UNINSTALLED_PACKAGES = ApplicationInfoFlags(PackageManager.MATCH_UNINSTALLED_PACKAGES)
        val MATCH_SYSTEM_ONLY = ApplicationInfoFlags(PackageManager.MATCH_SYSTEM_ONLY)
        val MATCH_DEBUG_TRIAGED_MISSING = ApplicationInfoFlags(0x10000000)
        val MATCH_DISABLED_COMPONENTS = ApplicationInfoFlags(PackageManager.MATCH_DISABLED_COMPONENTS)
        val MATCH_DISABLED_UNTIL_USED_COMPONENTS = ApplicationInfoFlags(PackageManager.MATCH_DISABLED_UNTIL_USED_COMPONENTS)
        val MATCH_INSTANT = ApplicationInfoFlags(0x00800000)
        val MATCH_STATIC_SHARED_AND_SDK_LIBRARIES = ApplicationInfoFlags(0x04000000)
        val GET_DISABLED_UNTIL_USED_COMPONENTS = ApplicationInfoFlags(PackageManager.GET_DISABLED_UNTIL_USED_COMPONENTS)
        val GET_UNINSTALLED_PACKAGES = ApplicationInfoFlags(PackageManager.GET_UNINSTALLED_PACKAGES)
        val MATCH_HIDDEN_UNTIL_INSTALLED_COMPONENTS = ApplicationInfoFlags(0x20000000)
        val MATCH_APEX = ApplicationInfoFlags(PackageManager.MATCH_APEX)
       // 1L << 32
//        val MATCH_ARCHIVED_PACKAGES = ApplicationInfoFlags(PackageManager.MATCH_ARCHIVED_PACKAGES)

        override val new: (Int) -> ApplicationInfoFlags = {
            ApplicationInfoFlags(it)
        }
    }
}

@JvmInline
value class ApplicationInfoPrivateFlags(override val value: Int) : Flags {
    companion object : FlagCompanion<ApplicationInfoPrivateFlags> {
        val SYSTEM = ApplicationInfoPrivateFlags(ApplicationInfo.FLAG_SYSTEM)
        val UPDATED_SYSTEM_APP = ApplicationInfoPrivateFlags(ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)

        override val new: (Int) -> ApplicationInfoPrivateFlags = {
            ApplicationInfoPrivateFlags(it)
        }
    }
}

@JvmInline
value class IntentFlags(override val value: Int) : Flags {
    companion object : FlagCompanion<IntentFlags> {
        val EMPTY = IntentFlags(0)

        val ACTIVITY_EXCLUDE_FROM_RECENTS = IntentFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        val ACTIVITY_FORWARD_RESULT = IntentFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT)

        override val new: (Int) -> IntentFlags = { IntentFlags(it) }
    }
}

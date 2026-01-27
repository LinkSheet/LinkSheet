package fe.linksheet.util

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi

interface LongFlags {
    val value: Long
    val inv: Long
        get() = value.inv()

    operator fun contains(flag: Long): Boolean {
        return (value and flag) != 0L
    }

    operator fun contains(flag: Int): Boolean {
        return (value.toInt() and flag) != 0
    }

    fun Long.remove(): Long {
        return this.and(inv)
    }

    fun Int.remove(): Int {
        return this.and(inv.toInt())
    }
}

interface LongFlagCompanion<T : LongFlags> {
    val new: (Long) -> T

    fun select(vararg flags: T): T {
        val sum = flags.sumOf { it.value }
        return new(sum)
    }

    fun T.inv(): T {
        return new(this@inv.inv)
    }
}

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
value class ResolveInfoFlags(override val value: Long) : LongFlags {
    constructor(value: Int) : this(value.toLong())

    companion object : LongFlagCompanion<ResolveInfoFlags> {
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

        override val new: (Long) -> ResolveInfoFlags = { ResolveInfoFlags(it) }
    }
}

@JvmInline
value class ApplicationInfoFlags(override val value: Long) : LongFlags {
    constructor(value: Int) : this(value.toLong())

    companion object : LongFlagCompanion<ApplicationInfoFlags> {
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
        @RequiresApi(Build.VERSION_CODES.Q)
        val MATCH_APEX = ApplicationInfoFlags(PackageManager.MATCH_APEX)
        // 1L << 32
//        val MATCH_ARCHIVED_PACKAGES = ApplicationInfoFlags(PackageManager.MATCH_ARCHIVED_PACKAGES)

        override val new: (Long) -> ApplicationInfoFlags = { ApplicationInfoFlags(it) }
    }
}

@JvmInline
value class ApplicationInfoPrivateFlags(override val value: Long) : LongFlags {
    constructor(value: Int) : this(value.toLong())

    companion object : LongFlagCompanion<ApplicationInfoPrivateFlags> {
        val SYSTEM = ApplicationInfoPrivateFlags(ApplicationInfo.FLAG_SYSTEM)
        val UPDATED_SYSTEM_APP = ApplicationInfoPrivateFlags(ApplicationInfo.FLAG_UPDATED_SYSTEM_APP)

        override val new: (Long) -> ApplicationInfoPrivateFlags = { ApplicationInfoPrivateFlags(it) }
    }
}

@JvmInline
value class IntentFlags(override val value: Long) : LongFlags {
    constructor(value: Int) : this(value.toLong())

    companion object : LongFlagCompanion<IntentFlags> {
        val EMPTY = IntentFlags(0)

        val ACTIVITY_EXCLUDE_FROM_RECENTS = IntentFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        val ACTIVITY_FORWARD_RESULT = IntentFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT)

        override val new: (Long) -> IntentFlags = { IntentFlags(it) }
    }
}

@JvmInline
value class ComponentInfoFlags(override val value: Long) : LongFlags {
    constructor(value: Int) : this(value.toLong())

    companion object : LongFlagCompanion<ComponentInfoFlags> {
        val EMPTY = ComponentInfoFlags(0)

        val GET_META_DATA = ComponentInfoFlags(PackageManager.GET_META_DATA)
        val GET_SHARED_LIBRARY_FILES = ComponentInfoFlags(PackageManager.GET_SHARED_LIBRARY_FILES)
        val MATCH_ALL = ComponentInfoFlags(PackageManager.MATCH_ALL)
        val MATCH_DEFAULT_ONLY = ComponentInfoFlags(PackageManager.MATCH_DEFAULT_ONLY)
        val MATCH_DISABLED_COMPONENTS = ComponentInfoFlags(PackageManager.MATCH_DISABLED_COMPONENTS)
        val MATCH_DISABLED_UNTIL_USED_COMPONENTS = ComponentInfoFlags(PackageManager.MATCH_DISABLED_UNTIL_USED_COMPONENTS)

        @RequiresApi(Build.VERSION_CODES.Q)
        val MATCH_DIRECT_BOOT_AUTO = ComponentInfoFlags(PackageManager.MATCH_DIRECT_BOOT_AUTO)
        val MATCH_DIRECT_BOOT_AWARE = ComponentInfoFlags(PackageManager.MATCH_DIRECT_BOOT_AWARE)
        val MATCH_DIRECT_BOOT_UNAWARE = ComponentInfoFlags(PackageManager.MATCH_DIRECT_BOOT_UNAWARE)
        val MATCH_SYSTEM_ONLY = ComponentInfoFlags(PackageManager.MATCH_SYSTEM_ONLY)
        val MATCH_UNINSTALLED_PACKAGES = ComponentInfoFlags(PackageManager.MATCH_UNINSTALLED_PACKAGES)

        override val new: (Long) -> ComponentInfoFlags = { ComponentInfoFlags(it) }
    }
}

@JvmInline
value class PackageInfoFlags(override val value: Long) : LongFlags {
    constructor(value: Int) : this(value.toLong())

    companion object : LongFlagCompanion<PackageInfoFlags> {
        val EMPTY = PackageInfoFlags(0)

        val GET_ACTIVITIES = PackageInfoFlags(PackageManager.GET_ACTIVITIES)
        val GET_CONFIGURATIONS = PackageInfoFlags(PackageManager.GET_CONFIGURATIONS)
        val GET_GIDS = PackageInfoFlags(PackageManager.GET_GIDS)
        val GET_INSTRUMENTATION = PackageInfoFlags(PackageManager.GET_INSTRUMENTATION)
        val GET_META_DATA = PackageInfoFlags(PackageManager.GET_META_DATA)
        val GET_PERMISSIONS = PackageInfoFlags(PackageManager.GET_PERMISSIONS)
        val GET_PROVIDERS = PackageInfoFlags(PackageManager.GET_PROVIDERS)
        val GET_RECEIVERS = PackageInfoFlags(PackageManager.GET_RECEIVERS)
        val GET_SERVICES = PackageInfoFlags(PackageManager.GET_SERVICES)
        val GET_SHARED_LIBRARY_FILES = PackageInfoFlags(PackageManager.GET_SHARED_LIBRARY_FILES)
        val GET_SIGNATURES = PackageInfoFlags(PackageManager.GET_SIGNATURES)
        @RequiresApi(Build.VERSION_CODES.P)
        val GET_SIGNING_CERTIFICATES = PackageInfoFlags(PackageManager.GET_SIGNING_CERTIFICATES)
        val GET_URI_PERMISSION_PATTERNS = PackageInfoFlags(PackageManager.GET_URI_PERMISSION_PATTERNS)
        val MATCH_UNINSTALLED_PACKAGES = PackageInfoFlags(PackageManager.MATCH_UNINSTALLED_PACKAGES)
        val MATCH_DISABLED_COMPONENTS = PackageInfoFlags(PackageManager.MATCH_DISABLED_COMPONENTS)
        val MATCH_DISABLED_UNTIL_USED_COMPONENTS = PackageInfoFlags(PackageManager.MATCH_DISABLED_UNTIL_USED_COMPONENTS)
        val MATCH_SYSTEM_ONLY = PackageInfoFlags(PackageManager.MATCH_SYSTEM_ONLY)
        @RequiresApi(Build.VERSION_CODES.Q)
        val MATCH_APEX = PackageInfoFlags(PackageManager.MATCH_APEX)
        @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
        val MATCH_ARCHIVED_PACKAGES = PackageInfoFlags(PackageManager.MATCH_ARCHIVED_PACKAGES)
        val GET_DISABLED_COMPONENTS = PackageInfoFlags(PackageManager.GET_DISABLED_COMPONENTS)
        val GET_DISABLED_UNTIL_USED_COMPONENTS = PackageInfoFlags(PackageManager.GET_DISABLED_UNTIL_USED_COMPONENTS)
        val GET_UNINSTALLED_PACKAGES = PackageInfoFlags(PackageManager.GET_UNINSTALLED_PACKAGES)
        val MATCH_DIRECT_BOOT_AWARE = PackageInfoFlags(PackageManager.MATCH_DIRECT_BOOT_AWARE)
        val MATCH_DIRECT_BOOT_UNAWARE = PackageInfoFlags(PackageManager.MATCH_DIRECT_BOOT_UNAWARE)
        @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
        val GET_ATTRIBUTIONS_LONG = PackageInfoFlags(PackageManager.GET_ATTRIBUTIONS_LONG)

        override val new: (Long) -> PackageInfoFlags = { PackageInfoFlags(it) }
    }
}

@JvmInline
value class ComponentEnabledStateFlags(override val value: Long) : LongFlags {
    constructor(value: Int) : this(value.toLong())

    companion object : LongFlagCompanion<ComponentEnabledStateFlags> {
        val EMPTY = ComponentEnabledStateFlags(0)

        val COMPONENT_ENABLED_STATE_DEFAULT = ComponentEnabledStateFlags(PackageManager.COMPONENT_ENABLED_STATE_DEFAULT)
        val COMPONENT_ENABLED_STATE_ENABLED = ComponentEnabledStateFlags(PackageManager.COMPONENT_ENABLED_STATE_ENABLED)
        val COMPONENT_ENABLED_STATE_DISABLED = ComponentEnabledStateFlags(PackageManager.COMPONENT_ENABLED_STATE_DISABLED)
        val COMPONENT_ENABLED_STATE_DISABLED_USER = ComponentEnabledStateFlags(PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER)
        val COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED = ComponentEnabledStateFlags(PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED)

        override val new: (Long) -> ComponentEnabledStateFlags = { ComponentEnabledStateFlags(it) }
    }
}

@JvmInline
value class ComponentEnabledFlags(override val value: Long) : LongFlags {
    constructor(value: Int) : this(value.toLong())

    companion object : LongFlagCompanion<ComponentEnabledFlags> {
        val EMPTY = ComponentEnabledFlags(0)

        val DONT_KILL_APP = ComponentEnabledFlags(PackageManager.DONT_KILL_APP)
        @RequiresApi(Build.VERSION_CODES.R)
        val SYNCHRONOUS = ComponentEnabledFlags(PackageManager.SYNCHRONOUS)

        override val new: (Long) -> ComponentEnabledFlags = { ComponentEnabledFlags(it) }
    }
}

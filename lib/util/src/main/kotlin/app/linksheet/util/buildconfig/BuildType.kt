@file:OptIn(ExperimentalAtomicApi::class)

package app.linksheet.util.buildconfig

import app.linksheet.util.buildconfig.BuildType.Unknown
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.properties.Delegates


enum class BuildType {
    Debug, Nightly, ReleaseDebug, Release, Unknown;
}

object StaticBuildInfo {
    private var setup = AtomicBoolean(false)

    lateinit var CurrentType: BuildType
    var IsDebug by Delegates.notNull<Boolean>()

    fun init(isDebug: Boolean, type: String) {
        if (!setup.compareAndSet(expectedValue = false, newValue = true)) return
        IsDebug = isDebug
        CurrentType = BuildType.entries.find { it.name.equals(type, ignoreCase = true) } ?: Unknown
    }

    fun isType(vararg types: BuildType): Boolean {
        return types.any { CurrentType == it }
    }
}

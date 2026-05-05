@file:OptIn(ExperimentalAtomicApi::class)

package app.linksheet.util.buildconfig

import app.linksheet.util.buildconfig.BuildType.Unknown
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.properties.Delegates


enum class BuildType {
    Debug, Nightly, ReleaseDebug, Release, Unknown;

    companion object {
        @Deprecated(
            "Use new API",
            replaceWith = ReplaceWith(
                "StaticBuildInfo.CurrentType",
                imports = ["app.linksheet.util.buildconfig"]
            )
        )
        val current by lazy { StaticBuildInfo.CurrentType }
    }
}

object StaticBuildInfo {
    private var setup = AtomicBoolean(false)

    fun init(isDebug: Boolean, type: String) {
        if (!setup.compareAndSet(expectedValue = false, newValue = true)) return
        IsDebug = isDebug
        CurrentType = BuildType.entries.find { it.name.equals(type, ignoreCase = true) } ?: Unknown
    }

    lateinit var CurrentType: BuildType
    var IsDebug by Delegates.notNull<Boolean>()
}

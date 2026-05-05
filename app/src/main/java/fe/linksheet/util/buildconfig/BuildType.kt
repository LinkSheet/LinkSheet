package fe.linksheet.util.buildconfig

import fe.linksheet.BuildConfig

enum class BuildType {
    Debug, Nightly, ReleaseDebug, Release, Unknown;

    companion object {
        @Deprecated(
            "Use new API",
            replaceWith = ReplaceWith(
                "app.linksheet.util.buildconfig.StaticBuildInfo.CurrentType",
                imports = ["app.linksheet.util.buildconfig"]
            )
        )
        val current by lazy {
            entries.find { it.name.equals(BuildConfig.BUILD_TYPE, ignoreCase = true) } ?: Unknown
        }
    }
}

object Build {
    @Deprecated(
        "Use new API",
        replaceWith = ReplaceWith(
            "app.linksheet.util.buildconfig.StaticBuildInfo.IsDebug",
            imports = ["app.linksheet.util.buildconfig"]
        )
    )
    val IsDebug = BuildConfig.DEBUG
}

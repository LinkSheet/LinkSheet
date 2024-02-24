package fe.linksheet.util

import fe.linksheet.BuildConfig

enum class BuildType {
    Debug, Nightly, Release;

    companion object {
        val current = BuildType.entries.find { it.name.equals(BuildConfig.BUILD_TYPE, ignoreCase = true) }!!
    }
}

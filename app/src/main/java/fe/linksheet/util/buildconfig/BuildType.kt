package fe.linksheet.util.buildconfig

import android.os.Build
import fe.linksheet.BuildConfig

enum class BuildType {
    Debug, Nightly, ReleaseDebug, Release, Unknown;

    val allowDebug by lazy { this == Debug && BuildConfig.DEBUG }
    val isTestRunner = Build.FINGERPRINT == "robolectric"

    companion object {
        val current by lazy {
            entries.find { it.name.equals(BuildConfig.BUILD_TYPE, ignoreCase = true) } ?: Unknown
        }
    }
}

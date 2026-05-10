package app.linksheet.feature.analytics.aptabase

import app.linksheet.api.BuildInfo
import app.linksheet.api.DeviceInfo
import app.linksheet.feature.analytics.service.TelemetryIdentityData
import app.linksheet.util.buildconfig.StaticBuildInfo

data class EnvironmentInfo(
    val isDebug: Boolean,
    val osName: String,
    val osVersion: String,
    val locale: String,
    val appVersion: String,
    val appBuildNumber: String,
    val deviceModel: String,
    val sdkVersion: String,
) {
    companion object {
        const val SDK_VERSION = "aptabase-kotlin@0.0.8"

        fun from(buildInfo: BuildInfo, deviceInfo: DeviceInfo, data: TelemetryIdentityData): EnvironmentInfo {
            // TODO: This should depend on the level, as previously implemented in TelemetryIdentityData
            val manufacturer = deviceInfo.manufacturer
            val model = deviceInfo.model

            return EnvironmentInfo(
                isDebug = StaticBuildInfo.IsDebug,
                osName = "Android",
                osVersion = deviceInfo.androidVersion,
                locale = data.data.getOrDefault("locale", "<null>"),
                appVersion = (buildInfo.versionName + "-" + buildInfo.flavor).lowercase(),
                appBuildNumber = buildInfo.versionCode.toString(),
                deviceModel = if (manufacturer != null && model != null) "$manufacturer/$model" else "<null>",
                sdkVersion = SDK_VERSION
            )
        }
    }
}

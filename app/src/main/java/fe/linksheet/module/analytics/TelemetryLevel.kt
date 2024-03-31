package fe.linksheet.module.analytics

import androidx.annotation.StringRes
import fe.android.preference.helper.OptionTypeMapper
import fe.linksheet.R

//sealed class TelemetryLevel(val name: String) {
//    data object Disabled : TelemetryLevel("disabled") {
//        override fun buildIdentity(context: Context, identity: String) = TelemetryIdentity.Anonymous
//    }
//
//    data object Basic : TelemetryLevel("basic") {
//        override fun buildIdentity(context: Context, identity: String) = TelemetryIdentity {
//            "identity" += identity
//            "app_info" += AppInfo.appInfo
//            "device_info" += AppInfo.getDeviceBasics(context) + AppInfo.deviceInfo
//        }
//    }
//
//    abstract fun buildIdentity(context: Context, identity: String): TelemetryIdentity
//
//    companion object : OptionTypeMapper<TelemetryLevel, String>({ it.name }, { arrayOf(Disabled, Basic) })
//}

sealed class TelemetryLevel(@StringRes val id: Int) {
    companion object : OptionTypeMapper<TelemetryLevel, String>({ it.id.toString() }, {
        arrayOf(Disabled, Minimal, Standard, Exhaustive)
    })

    data object Disabled : TelemetryLevel(R.string.telemetry_level_disabled)

    // Ping on first launch / after update
    data object Minimal : TelemetryLevel(R.string.telemetry_level_minimal)

    // Minimal + aggregated statistics
    data object Standard : TelemetryLevel(R.string.telemetry_level_standard)

    // Standard + navigation, interaction and preferences
    data object Exhaustive : TelemetryLevel(R.string.telemetry_level_exhaustive)
}

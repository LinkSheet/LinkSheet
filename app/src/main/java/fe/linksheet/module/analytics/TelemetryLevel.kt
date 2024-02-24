package fe.linksheet.module.analytics

import android.content.Context
import fe.android.preference.helper.OptionTypeMapper
import fe.gson.extension.json.`object`.plus
import fe.linksheet.util.AppInformation

sealed class TelemetryLevel(val name: String) {
    data object Disabled : TelemetryLevel("disabled") {
        override fun buildIdentity(context: Context, identity: String) = TelemetryIdentity.Anonymous
    }

    data object Basic : TelemetryLevel("basic") {
        override fun buildIdentity(context: Context, identity: String) = TelemetryIdentity {
            "identity" += identity
            "app_info" += AppInformation.appInfo
            "device_info" += AppInformation.getDeviceBasics(context) + AppInformation.deviceInfo
        }
    }

    abstract fun buildIdentity(context: Context, identity: String): TelemetryIdentity

    companion object : OptionTypeMapper<TelemetryLevel, String>({ it.name }, { arrayOf(Disabled, Basic) })
}



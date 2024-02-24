package fe.linksheet.module.analytics

import android.content.Context
import fe.android.preference.helper.OptionTypeMapper
import fe.gson.extension.json.`object`.plus
import fe.linksheet.util.LinkSheetAppInfo

sealed class TelemetryLevel(val name: String) {
    data object Disabled : TelemetryLevel("Disabled") {
        override fun buildIdentity(context: Context, identity: String) = TelemetryIdentity.Anonymous
    }

    data object Basic : TelemetryLevel("Basic") {
        override fun buildIdentity(context: Context, identity: String) = TelemetryIdentity {
            "identity" += identity
            "app_info" += LinkSheetAppInfo.appInfo
            "device_info" += LinkSheetAppInfo.getDeviceBasics(context) + LinkSheetAppInfo.deviceInfo
        }
    }

    abstract fun buildIdentity(context: Context, identity: String): TelemetryIdentity

    companion object Companion : OptionTypeMapper<TelemetryLevel, String>({ it.name }, { arrayOf(Disabled, Basic) })
}



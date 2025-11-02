package fe.linksheet.module.analytics

import android.content.Context
import android.os.Build
import androidx.annotation.StringRes
import fe.android.preference.helper.OptionTypeMapper
import fe.linksheet.BuildConfig
import fe.linksheet.R
import fe.linksheet.util.extension.android.getCurrentLanguageTag
import fe.linksheet.util.buildconfig.BuildType

@JvmInline
value class TelemetryIdentityData(val data: Map<String, String>)

sealed class TelemetryIdentity(
    @param:StringRes val id: Int,
    val data: Map<String, String>,
    private vararg val mergeWith: TelemetryIdentity,
) {
    companion object : OptionTypeMapper<TelemetryIdentity, String>({ it.id.toString() }, {
        arrayOf(Basic, Full)
    })

    @OptIn(ExperimentalStdlibApi::class)
    fun create(
        context: Context,
        identity: String,
        startMillis: Long = System.currentTimeMillis(),
    ): TelemetryIdentityData {
        val map = getRuntime(context)
        for (it in mergeWith) map.putAll(it.data)

        map.putAll(data)

        // Unix seconds (e.g. 1711894875) => 10 chars as hex => 8
        val session = (startMillis / 1000).toInt().toHexString()
        // 21 + 8 => 29 < 36 max chars
        map["sessionId"] = "$identity@$session"

        return TelemetryIdentityData(map)
    }

    protected open fun getRuntime(context: Context): MutableMap<String, String> = mutableMapOf()


    data object Basic : TelemetryIdentity(
        R.string.telemetry_identity_basic,
        mapOf(
            "type" to BuildType.current.name,
            "flavor" to BuildConfig.FLAVOR,
            "version_name" to if (BuildType.current == BuildType.Release) BuildConfig.VERSION_NAME else BuildConfig.COMMIT.substring(
                0,
                6
            ),
            "version_code" to BuildConfig.VERSION_CODE.toString(),
            "android_version" to Build.VERSION.RELEASE,
        )
    )

    data object Full : TelemetryIdentity(
        R.string.telemetry_identity_full,
        mapOf("manufacturer" to Build.MANUFACTURER, "model" to Build.MODEL),
        Basic
    ) {
        override fun getRuntime(context: Context): MutableMap<String, String> {
            return mutableMapOf("locale" to context.getCurrentLanguageTag())
        }
    }
}

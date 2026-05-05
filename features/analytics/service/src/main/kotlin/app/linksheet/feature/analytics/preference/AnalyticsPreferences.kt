@file:OptIn(SensitivePreference::class)

package app.linksheet.feature.analytics.preference

import app.linksheet.api.PreferenceRegistry
import app.linksheet.api.SensitivePreference
import app.linksheet.api.mapped
import app.linksheet.feature.analytics.service.TelemetryIdentity
import app.linksheet.feature.analytics.service.TelemetryLevel
import fe.android.preference.helper.Preference
import io.viascom.nanoid.NanoId


interface AnalyticsPreferences {
    @SensitivePreference
    val telemetryId: Preference.Init
    @SensitivePreference
    val telemetryIdentity: Preference.Mapped<TelemetryIdentity, String>
    @SensitivePreference
    val telemetryLevel: Preference.Mapped<TelemetryLevel, String>
    val telemetryShowInfoDialog: Preference.Boolean
}


fun analyticsPreferences(registry: PreferenceRegistry): AnalyticsPreferences {
    return object : AnalyticsPreferences {
        override val telemetryId = registry.string("telemetry_id") { NanoId.generate() }
        override val telemetryIdentity = registry.mapped("telemetry_identity_2", TelemetryIdentity.Basic, TelemetryIdentity)
        override val telemetryLevel = registry.mapped("telemetry_level", TelemetryLevel.Standard, TelemetryLevel)
        override val telemetryShowInfoDialog = registry.boolean("telemetry_dialog", true)
    }
}

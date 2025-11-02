package fe.linksheet.module.analytics

import androidx.annotation.StringRes
import fe.android.preference.helper.OptionTypeMapper
import fe.linksheet.R

sealed class TelemetryLevel(@param:StringRes val titleId: Int, @param:StringRes val descriptionId: Int) {
    companion object : OptionTypeMapper<TelemetryLevel, String>({ it.titleId.toString() }, {
        arrayOf(Disabled, Minimal, Standard, Exhaustive)
    })

    abstract fun canSendEvent(event: AnalyticsEvent): Boolean

    data object Disabled : TelemetryLevel(R.string.telemetry_level_disabled, R.string.telemetry_level_disabled_description) {
        override fun canSendEvent(event: AnalyticsEvent): Boolean {
            return false
        }
    }

    // Ping on first launch / after update
    data object Minimal : TelemetryLevel(R.string.telemetry_level_minimal, R.string.telemetry_level_minimal_description) {
        override fun canSendEvent(event: AnalyticsEvent): Boolean {
            if (event is AppStart.FirstRun || event is AppStart.Updated) {
                return true
            }

            // TODO: Is that all?
            return false
        }
    }

    // Minimal + aggregated statistics
    data object Standard : TelemetryLevel(R.string.telemetry_level_standard, R.string.telemetry_level_standard_description) {
        override fun canSendEvent(event: AnalyticsEvent): Boolean {
            if (Minimal.canSendEvent(event)) {
                return true
            }

            // TODO: How should we aggregate stats?
            return false
        }
    }

    // Standard + navigation, interaction and preferences
    data object Exhaustive : TelemetryLevel(R.string.telemetry_level_exhaustive, R.string.telemetry_level_exhaustive_description) {
        override fun canSendEvent(event: AnalyticsEvent): Boolean {
            if (Standard.canSendEvent(event)) {
                return true
            }

            if (event is AnalyticsEvent.Navigate) {
                return true
            }

            // TODO: Figure out how interaction events should be collected
            return false
        }
    }
}


fun TelemetryLevel?.canSendEvent(event: AnalyticsEvent): Boolean {
    return this?.canSendEvent(event) == true
}

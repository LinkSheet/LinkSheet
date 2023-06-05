package fe.linksheet.module.viewmodel.util

import android.os.Build
import fe.android.preference.helper.PreferenceRepository
import fe.linksheet.lineSeparator
import fe.linksheet.module.log.LogEntry
import fe.linksheet.module.log.LogHasher
import fe.linksheet.module.log.LoggerFactory
import fe.linksheet.module.preference.Preferences
import java.lang.StringBuilder


class LogViewCommon(
    val preferenceRepository: PreferenceRepository,
    private val loggerFactory: LoggerFactory
) {
    private fun logPreferences(
        redact: Boolean
    ) = Preferences.log(preferenceRepository) + Preferences.logPackages(
        if (redact) loggerFactory.logHasher else LogHasher.NoOpHasher, preferenceRepository
    )

    fun buildClipboardText(
        includeFingerprint: Boolean,
        includePreferences: Boolean,
        redactLog: Boolean,
        log: (StringBuilder) -> Unit
    ) = buildString {
        log(this)
        if (includeFingerprint) {
            append(lineSeparator, "device_fingerprint=", Build.FINGERPRINT)
        }

        if (includePreferences) {
            append(lineSeparator, "preferences:")
            logPreferences(redactLog).joinTo(
                this,
                separator = lineSeparator,
                prefix = lineSeparator
            )
        }
    }
}

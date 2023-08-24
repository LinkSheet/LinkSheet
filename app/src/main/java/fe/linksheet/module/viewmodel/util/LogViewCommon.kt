package fe.linksheet.module.viewmodel.util

import android.os.Build
import fe.linksheet.module.preference.AppPreferenceRepository

import fe.linksheet.BuildConfig
import fe.linksheet.lineSeparator
import fe.linksheet.module.log.Logger
import fe.linksheet.module.preference.AppPreferences


class LogViewCommon(
    val preferenceRepository: AppPreferenceRepository,
    private val logger: Logger
) {
    private fun logPreferences(redact: Boolean): List<String?> {
        return AppPreferences.loggablePreferences.map {
            preferenceRepository.getAnyAsString(it)
        } + AppPreferences.logPackages(redact, logger, preferenceRepository)
    }

    fun buildClipboardText(
        includeFingerprint: Boolean,
        includePreferences: Boolean,
        redactLog: Boolean,
        log: (StringBuilder) -> Unit
    ) = buildString {
        log(this)
        append(
            lineSeparator,
            "linksheet_version=",
            BuildConfig.VERSION_NAME,
            ",code=" + BuildConfig.VERSION_CODE
        )

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

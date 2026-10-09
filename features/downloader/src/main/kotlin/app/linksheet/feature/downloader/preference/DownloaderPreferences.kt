package app.linksheet.feature.downloader.preference

import app.linksheet.api.PreferenceRegistry
import app.linksheet.api.mapped
import app.linksheet.feature.downloader.core.DownloaderMode
import fe.android.preference.helper.Preference

interface DownloaderPreferences {
    val enable: Preference.Boolean
    val mode: Preference.Mapped<DownloaderMode, String>
    val checkUrlMimeType: Preference.Boolean
    val requestTimeout: Preference.Int
}

fun downloaderPreferences(registry: PreferenceRegistry): DownloaderPreferences {
    return object : DownloaderPreferences {
        override val enable = registry.boolean("enable_downloader", false)
        override val mode = registry.mapped("downloader_mode", DownloaderMode.Auto, DownloaderMode)
        override val checkUrlMimeType = registry.boolean("downloaderCheckUrlMimeType", false)
        override val requestTimeout = registry.int("downloader_request_timeout", 15)
    }
}

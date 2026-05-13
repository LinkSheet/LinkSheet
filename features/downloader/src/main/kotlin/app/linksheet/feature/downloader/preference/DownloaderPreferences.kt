package app.linksheet.feature.downloader.preference

import app.linksheet.api.PreferenceRegistry
import fe.android.preference.helper.Preference

interface DownloaderPreferences {
    val enable: Preference.Boolean
    val checkUrlMimeType: Preference.Boolean
    val requestTimeout: Preference.Int
}

fun downloaderPreferences(registry: PreferenceRegistry): DownloaderPreferences {
    return object : DownloaderPreferences {
        override val enable = registry.boolean("enable_downloader", false)
        override val checkUrlMimeType = registry.boolean("downloaderCheckUrlMimeType", false)
        override val requestTimeout = registry.int("downloader_request_timeout", 15)
    }
}

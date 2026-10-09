package app.linksheet.feature.downloader.core

import fe.android.preference.helper.OptionTypeMapper

sealed class DownloaderMode(val name: String) {
    data object Auto : DownloaderMode("auto")
    data object Manual : DownloaderMode("manual")

    companion object : OptionTypeMapper<DownloaderMode, String>(
        key = { it.name },
        options = { arrayOf(Auto, Manual) }
    )
}

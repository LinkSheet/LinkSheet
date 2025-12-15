package fe.linksheet.module.preference.app

import app.linksheet.api.PreferenceRegistry

class Downloader(registry: PreferenceRegistry) {
    val enable = registry.boolean("enable_downloader", false)
    val checkUrlMimeType = registry.boolean("downloaderCheckUrlMimeType", false)
}

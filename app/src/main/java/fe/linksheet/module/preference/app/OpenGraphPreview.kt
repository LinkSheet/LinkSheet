package fe.linksheet.module.preference.app

import app.linksheet.api.PreferenceRegistry

class OpenGraphPreview(registry: PreferenceRegistry) {
    val enable = registry.boolean("url_bar_preview")
    val skipBrowser = registry.boolean("url_bar_preview_skip_browser")
}

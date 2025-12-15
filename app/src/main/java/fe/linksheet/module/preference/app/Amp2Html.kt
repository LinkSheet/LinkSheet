package fe.linksheet.module.preference.app

import app.linksheet.api.PreferenceRegistry

class Amp2Html(registry: PreferenceRegistry) {
    val enable = registry.boolean("enable_amp2html", false)
    val localCache = registry.boolean("amp2html_local_cache", true)
    val externalService = registry.boolean("amp2html_external_service", false)
    val allowDarknets = registry.boolean("amp2html_allow_darknets", false)
    val allowLocalNetwork = registry.boolean("amp2html_allow_local_network", false)
    val skipBrowser = registry.boolean("amp2html_skip_browser", true)
}

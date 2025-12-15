package fe.linksheet.module.preference.app

import app.linksheet.api.PreferenceRegistry

class FollowRedirects(registry: PreferenceRegistry) {
    val enable = registry.boolean("follow_redirects", false)
    val localCache = registry.boolean("follow_redirects_local_cache", true)
    val externalService = registry.boolean("follow_redirects_external_service", false)
    val onlyKnownTrackers = registry.boolean("follow_only_known_trackers", false)
    val allowDarknets = registry.boolean("follow_redirects_allow_darknets", false)
    val allowLocalNetwork = registry.boolean("follow_redirects_allow_local_network", false)
    val skipBrowser = registry.boolean("follow_redirects_skip_browser", true)
}

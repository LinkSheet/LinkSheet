package fe.linksheet.module.preference.app

import app.linksheet.api.PreferenceRegistry
import app.linksheet.api.mapped
import fe.linksheet.module.resolver.FollowRedirectsMode

class FollowRedirects(registry: PreferenceRegistry) {
    val enable = registry.boolean("follow_redirects", false)
    val mode = registry.mapped("follow_redirects_mode", FollowRedirectsMode.Auto, FollowRedirectsMode)
    val aggressive = registry.boolean("follow_redirects_aggressive", false)
    val localCache = registry.boolean("follow_redirects_local_cache", true)
    val externalService = registry.boolean("follow_redirects_external_service", false)
    val onlyKnownTrackers = registry.boolean("follow_only_known_trackers", false)
    val allowDarknets = registry.boolean("follow_redirects_allow_darknets", false)
    val allowLocalNetwork = registry.boolean("follow_redirects_allow_local_network", false)
    val skipBrowser = registry.boolean("follow_redirects_skip_browser", true)
}

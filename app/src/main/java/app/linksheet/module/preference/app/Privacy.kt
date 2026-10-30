package app.linksheet.module.preference.app

import app.linksheet.api.PreferenceRegistry

class PrivacyPreferences(registry: PreferenceRegistry) {
    val showLinkSheetAsReferrer = registry.boolean("show_as_referrer")
    val incognito = PrivacyIncognitoPreferences(registry)
}

class PrivacyIncognitoPreferences(registry: PreferenceRegistry) {
    val enable = registry.boolean("privacy_incognito__enable")
    val disableLogs = registry.boolean("privacy_incognito__disable_logs")
    val disableLogsPersistence = registry.boolean("privacy_incognito__disable_log_persistence")
}

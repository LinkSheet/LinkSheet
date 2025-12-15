package fe.linksheet.module.preference.app

import app.linksheet.api.PreferenceRegistry

class Notifications(registry: PreferenceRegistry) {
    val urlCopiedToast = registry.boolean("url_copied_toast", true)
    val downloadStartedToast = registry.boolean("download_started_toast", true)
    val openingWithAppToast = registry.boolean("opening_with_app_toast", true)
    val resolveViaToast = registry.boolean("resolve_via_toast", true)
    val resolveViaFailedToast = registry.boolean("resolve_via_failed_toast", true)
}

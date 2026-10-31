package fe.linksheet.module.resolver

import fe.android.preference.helper.OptionTypeMapper
import fe.composekit.core.AndroidAppPackage
import fe.kotlin.extension.iterable.mapToSet
import fe.linksheet.module.repository.DisableInAppBrowserInSelectedRepository
import kotlinx.coroutines.flow.first

class InAppBrowserHandler(
    private val disableInPackages: suspend () -> Set<String>
) {
    constructor(repository: DisableInAppBrowserInSelectedRepository) : this(
        disableInPackages = {
            repository.getAll().first().mapToSet { it.packageName }
        }
    )

    sealed class InAppBrowserMode(val value: String) {
        data object UseAppSettings : InAppBrowserMode("use_app_settings")
        data object AlwaysDisableInAppBrowser : InAppBrowserMode("always_disable_in_app_browser")
        data object DisableInSelectedApps : InAppBrowserMode("disable_in_selected_apps")

        companion object : OptionTypeMapper<InAppBrowserMode, String>(
            { it.value },
            { arrayOf(UseAppSettings, AlwaysDisableInAppBrowser, DisableInSelectedApps) }
        )

        override fun toString(): String {
            return value
        }
    }

    suspend fun shouldAllowCustomTab(
        referrer: AndroidAppPackage?,
        inAppBrowserMode: InAppBrowserMode
    ): Boolean {
        return when (inAppBrowserMode) {
            InAppBrowserMode.AlwaysDisableInAppBrowser -> false
            InAppBrowserMode.UseAppSettings -> true
            InAppBrowserMode.DisableInSelectedApps -> {
                val packageName = referrer?.packageName ?: return true
                val selectedApps = disableInPackages()
                packageName !in selectedApps
            }
        }
    }
}

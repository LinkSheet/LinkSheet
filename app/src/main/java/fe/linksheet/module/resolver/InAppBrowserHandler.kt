package fe.linksheet.module.resolver

import android.net.Uri
import fe.android.preference.helper.OptionTypeMapper
import fe.kotlin.extension.iterable.mapToSet
import fe.linksheet.module.repository.DisableInAppBrowserInSelectedRepository
import kotlinx.coroutines.flow.first

class InAppBrowserHandler(
    private val disableInAppBrowserInSelectedRepository: DisableInAppBrowserInSelectedRepository,
) {
    sealed class InAppBrowserMode(val value: String) {
        object UseAppSettings : InAppBrowserMode("use_app_settings")
        object AlwaysDisableInAppBrowser : InAppBrowserMode("always_disable_in_app_browser")
        object DisableInSelectedApps : InAppBrowserMode("disable_in_selected_apps")

        companion object Companion : OptionTypeMapper<InAppBrowserMode, String>(
            { it.value },
            { arrayOf(UseAppSettings, AlwaysDisableInAppBrowser, DisableInSelectedApps) }
        )
    }

    suspend fun shouldAllowCustomTab(referrer: Uri?, inAppBrowserMode: InAppBrowserMode): Boolean {
        return when (inAppBrowserMode) {
            InAppBrowserMode.AlwaysDisableInAppBrowser -> false
            InAppBrowserMode.UseAppSettings -> true
            InAppBrowserMode.DisableInSelectedApps -> {
                val selectedApps = disableInAppBrowserInSelectedRepository.getAll().first().mapToSet {
                    it.packageName
                }

                val packageName = referrer?.authority
                if (packageName != null) {
                    packageName in selectedApps
                } else {
                    true
                }
            }
        }
    }
}

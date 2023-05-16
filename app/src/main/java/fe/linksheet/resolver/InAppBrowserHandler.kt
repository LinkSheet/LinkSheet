package fe.linksheet.resolver

import android.net.Uri
import com.tasomaniac.openwith.resolver.BrowserHandler
import fe.linksheet.activity.bottomsheet.BottomSheetViewModel
import fe.linksheet.extension.mapToSet
import fe.linksheet.module.preference.OptionTypeMapper
import fe.linksheet.module.preference.Persister
import fe.linksheet.module.preference.PreferenceRepository
import fe.linksheet.module.preference.Preferences
import fe.linksheet.module.preference.Reader
import fe.linksheet.module.preference.TypeMapper
import fe.linksheet.util.keyedMap
import fe.linksheet.util.lazyKeyedMap
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

object InAppBrowserHandler : KoinComponent {
    private val preferenceRepository by inject<PreferenceRepository>()

    sealed class InAppBrowserMode(val value: String) {
        object UseAppSettings : InAppBrowserMode("use_app_settings")
        object AlwaysDisableInAppBrowser : InAppBrowserMode("always_disable_in_app_browser")
        object DisableInSelectedApps : InAppBrowserMode("disable_in_selected_apps")

        companion object Companion : OptionTypeMapper<InAppBrowserMode, String>(
            { it.value },
            { arrayOf(UseAppSettings, AlwaysDisableInAppBrowser, DisableInSelectedApps) }
        )
    }

    suspend fun shouldAllowCustomTab(
        referrer: Uri?,
        viewModel: BottomSheetViewModel,
    ): Boolean {
        return when (preferenceRepository.get(Preferences.inAppBrowserMode)) {
            InAppBrowserMode.AlwaysDisableInAppBrowser -> false
            InAppBrowserMode.UseAppSettings -> true
            InAppBrowserMode.DisableInSelectedApps -> {
                val selectedApps = viewModel.getDisableInAppBrowserInSelected().mapToSet {
                    it.packageName
                }

                val packageName = referrer?.authority
                if (packageName != null) {
                    packageName in selectedApps
                } else {
                    Timber.tag("InAppBrowserHandler")
                        .d("No referer found, falling back to allowing in app browsers")
                    true
                }
            }
        }
    }
}
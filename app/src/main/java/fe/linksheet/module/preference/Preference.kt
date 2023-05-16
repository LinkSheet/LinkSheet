package fe.linksheet.module.preference

import com.tasomaniac.openwith.resolver.BrowserHandler
import fe.linksheet.resolver.InAppBrowserHandler
import fe.linksheet.ui.theme.Theme

object Preferences {
    val enableCopyButton = booleanPreference("enable_copy_button")
    val hideAfterCopying = booleanPreference("hide_after_copying")
    val singleTap = booleanPreference("single_tap")
    val usageStatsSorting = booleanPreference("usage_stats_sorting")
    val browserMode = mappedPreference("browser_mode", BrowserHandler.BrowserMode.AlwaysAsk, BrowserHandler.BrowserMode.Companion)
    val selectedBrowser = stringPreference("selected_browser")
    val inAppBrowserMode = mappedPreference("in_app_browser_mode", InAppBrowserHandler.InAppBrowserMode.UseAppSettings, InAppBrowserHandler.InAppBrowserMode.Companion)
    val enableSendButton = booleanPreference("enable_send_button")
    val alwaysShowPackageName = booleanPreference("always_show_package_name")
    val disableToasts = booleanPreference("disable_toasts")
    val gridLayout = booleanPreference("grid_layout")
    val useClearUrls = booleanPreference("use_clear_urls")
    val useFastForwardRules = booleanPreference("fast_forward_rules")
    val enableLibRedirect = booleanPreference("enable_lib_redirect")
    val followRedirects = booleanPreference("follow_redirects")
    val followRedirectsLocalCache = booleanPreference("follow_redirects_local_cache")
    val followRedirectsExternalService = booleanPreference("follow_redirects_external_service")
    val followOnlyKnownTrackers = booleanPreference("follow_only_known_trackers")
    val theme = mappedPreference("theme", Theme.System, Theme.Companion)
    val dontShowFilteredItem = booleanPreference("dont_show_filtered_item")
    val useTextShareCopyButtons = booleanPreference("use_text_share_copy_buttons")
    val previewUrl = booleanPreference("preview_url")
    val enableDownloader = booleanPreference("enable_downloader")
    val downloaderCheckUrlMimeType = booleanPreference("downloaderCheckUrlMimeType")
}

sealed class BasePreference<T, NT>(val key: String, val default: NT) {
    class PreferenceNullable<T>(key: String, default: T?) : BasePreference<T, T?>(key, default)

    class Preference<T>(key: String, default: T) : BasePreference<T, T>(key, default)

    class MappedPreference<T, M>(key: String, default: T, private val mapper: TypeMapper<T, M>) : BasePreference<T, T>(key, default) {
        val defaultMapped = persist(default)

        fun read(mapped: M) = mapper.reader(mapped)
        fun persist(value: T) = mapper.persister(value)
    }
}

private fun stringPreference(key: String, default: String? = null) = BasePreference.PreferenceNullable(key, default)
private fun booleanPreference(key: String, default: Boolean = false) = BasePreference.Preference(key, default)
private fun <T, M> mappedPreference(key: String, default: T, mapper: TypeMapper<T, M>) = BasePreference.MappedPreference(key, default, mapper)
package fe.linksheet.module.preference

import fe.android.preference.helper.BasePreference.InitPreference.Companion.stringPreference
import fe.android.preference.helper.BasePreference.MappedPreference.Companion.mappedPreference
import fe.android.preference.helper.BasePreference.Preference.Companion.booleanPreference
import fe.android.preference.helper.BasePreference.Preference.Companion.intPreference
import fe.android.preference.helper.BasePreference.PreferenceNullable.Companion.stringPreference
import fe.android.preference.helper.PreferenceRepository
import fe.kotlin.extension.toHexString
import fe.linksheet.module.log.Logger
import fe.linksheet.module.log.PackageProcessor
import fe.linksheet.module.log.loggerHmac
import fe.linksheet.module.resolver.BrowserHandler
import fe.linksheet.module.resolver.InAppBrowserHandler
import fe.linksheet.ui.Theme
import fe.linksheet.util.CryptoUtil

object Preferences {
    val enableCopyButton = booleanPreference("enable_copy_button")
    val hideAfterCopying = booleanPreference("hide_after_copying")
    val singleTap = booleanPreference("single_tap")
    val usageStatsSorting = booleanPreference("usage_stats_sorting")


    val browserMode = mappedPreference(
        "browser_mode",
        BrowserHandler.BrowserMode.AlwaysAsk,
        BrowserHandler.BrowserMode.Companion
    )
    val selectedBrowser = stringPreference("selected_browser")

    val inAppBrowserMode = mappedPreference(
        "in_app_browser_mode",
        BrowserHandler.BrowserMode.AlwaysAsk,
        BrowserHandler.BrowserMode.Companion
    )


    val selectedInAppBrowser = stringPreference("selected_in_app_browser")
    val unifiedPreferredBrowser = booleanPreference("unified_preferred_browser", true)

    val inAppBrowserSettings = mappedPreference(
        "in_app_browser_setting",
        InAppBrowserHandler.InAppBrowserMode.UseAppSettings,
        InAppBrowserHandler.InAppBrowserMode.Companion
    )
    val enableSendButton = booleanPreference("enable_send_button")
    val alwaysShowPackageName = booleanPreference("always_show_package_name")
    val disableToasts = booleanPreference("disable_toasts")
    val gridLayout = booleanPreference("grid_layout")
    val useClearUrls = booleanPreference("use_clear_urls")
    val useFastForwardRules = booleanPreference("fast_forward_rules")
    val enableLibRedirect = booleanPreference("enable_lib_redirect")
    val followRedirects = booleanPreference("follow_redirects")
    val followRedirectsLocalCache = booleanPreference("follow_redirects_local_cache", true)
    val followRedirectsExternalService = booleanPreference("follow_redirects_external_service")
    val followOnlyKnownTrackers = booleanPreference("follow_only_known_trackers")
    val followRedirectsBuiltInCache = booleanPreference("follow_redirects_builtin_cache", true)
    val theme = mappedPreference("theme", Theme.System, Theme.Companion)
    val dontShowFilteredItem = booleanPreference("dont_show_filtered_item")
    val useTextShareCopyButtons = booleanPreference("use_text_share_copy_buttons")
    val previewUrl = booleanPreference("preview_url")
    val enableDownloader = booleanPreference("enable_downloader")
    val downloaderCheckUrlMimeType = booleanPreference("downloaderCheckUrlMimeType")

    val enableIgnoreLibRedirectButton = booleanPreference("enable_ignore_lib_redirect_button")

    val requestTimeout = intPreference("follow_redirects_timeout", 15)

    val enableAmp2Html = booleanPreference("enable_amp2html")
    val amp2HtmlLocalCache = booleanPreference("amp2html_local_cache", true)
    val amp2HtmlExternalService = booleanPreference("amp2html_external_service")
    val amp2HtmlBuiltInCache = booleanPreference("amp2html_builtin_cache", true)

    val enableRequestPrivateBrowsingButton =
        booleanPreference("enable_request_private_browsing_button")

    val logKey = stringPreference("log_key") {
        CryptoUtil.getRandomBytes(loggerHmac.keySize).toHexString()
    }

    val firstRun = booleanPreference("first_run", true)

    val featureFlagShizuku = booleanPreference("feature_flag_shizuku")

    private val preferencesLoggable = listOf(
        enableCopyButton,
        hideAfterCopying,
        singleTap,
        usageStatsSorting,
        browserMode,
        inAppBrowserMode,
        unifiedPreferredBrowser,
        inAppBrowserSettings,
        enableSendButton,
        alwaysShowPackageName,
        disableToasts,
        gridLayout,
        useClearUrls,
        useFastForwardRules,
        enableLibRedirect,
        followRedirects,
        followRedirects,
        followRedirectsLocalCache,
        followRedirectsExternalService,
        followOnlyKnownTrackers,
        theme,
        dontShowFilteredItem,
        useTextShareCopyButtons,
        previewUrl,
        enableDownloader,
        downloaderCheckUrlMimeType,
        enableIgnoreLibRedirectButton,
        requestTimeout,
        enableRequestPrivateBrowsingButton,
        enableAmp2Html,
        amp2HtmlLocalCache,
        amp2HtmlExternalService,
        followRedirectsBuiltInCache,
        amp2HtmlBuiltInCache,
        featureFlagShizuku,
        firstRun
    )

    private val preferencesPackage = listOf(selectedBrowser, selectedInAppBrowser)

    fun log(
        repository: PreferenceRepository
    ) = preferencesLoggable.map { "${it.key}=${repository.getAsString(it)}" }

    fun logPackages(
        redact: Boolean,
        logger: Logger,
        repository: PreferenceRepository
    ) = preferencesPackage.map {
        buildString {
            val value = repository.getString(it)
            append(it.key)

            if (value != null) {
                append(logger.dumpParameterToString(redact, value, PackageProcessor))
            } else {
                append("null")
            }
        }
    }
}



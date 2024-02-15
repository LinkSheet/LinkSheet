package fe.linksheet.module.preference


import com.google.gson.JsonArray
import fe.android.preference.helper.Preferences
import fe.gson.dsl.jsonArray
import fe.gson.dsl.jsonObject
import fe.linksheet.module.log.Logger
import fe.linksheet.module.log.hasher.PackageProcessor
import fe.linksheet.module.log.loggerHmac
import fe.linksheet.module.resolver.BrowserHandler
import fe.linksheet.module.resolver.InAppBrowserHandler
import fe.linksheet.ui.Theme
import fe.linksheet.util.CryptoUtil

object AppPreferences : Preferences() {
    val enableCopyButton = booleanPreference("enable_copy_button")
    val hideAfterCopying = booleanPreference("hide_after_copying")
    val singleTap = booleanPreference("single_tap")
    val usageStatsSorting = booleanPreference("usage_stats_sorting")

    val browserMode = mappedPreference(
        "browser_mode",
        BrowserHandler.BrowserMode.AlwaysAsk,
        BrowserHandler.BrowserMode.Companion
    )

    @SensitivePreference
    val selectedBrowser = stringPreference("selected_browser")

    val inAppBrowserMode = mappedPreference(
        "in_app_browser_mode",
        BrowserHandler.BrowserMode.AlwaysAsk,
        BrowserHandler.BrowserMode.Companion
    )

    @SensitivePreference
    val selectedInAppBrowser = stringPreference("selected_in_app_browser")
    val unifiedPreferredBrowser = booleanPreference("unified_preferred_browser", true)

    val inAppBrowserSettings = mappedPreference(
        "in_app_browser_setting",
        InAppBrowserHandler.InAppBrowserMode.UseAppSettings,
        InAppBrowserHandler.InAppBrowserMode.Companion
    )
    val enableSendButton = booleanPreference("enable_send_button")
    val alwaysShowPackageName = booleanPreference("always_show_package_name")
    val urlCopiedToast = booleanPreference("url_copied_toast", true)
    val downloadStartedToast = booleanPreference("download_started_toast", true)
    val openingWithAppToast = booleanPreference("opening_with_app_toast", true)
    val resolveViaToast = booleanPreference("resolve_via_toast", true)
    val resolveViaFailedToast = booleanPreference("resolve_via_failed_toast", true)

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

    val enableRequestPrivateBrowsingButton = booleanPreference(
        "enable_request_private_browsing_button"
    )

    @SensitivePreference
    val useTimeMs = longPreference("use_time", 0)

    val showLinkSheetAsReferrer = booleanPreference("show_as_referrer")
    val devModeEnabled = booleanPreference("dev_mode_enabled")

    @OptIn(ExperimentalStdlibApi::class)
    @SensitivePreference
    val logKey = stringPreference("log_key") {
        CryptoUtil.getRandomBytes(loggerHmac.keySize).toHexString()
    }

    val firstRun = booleanPreference("first_run", true)
    val showDiscordBanner = booleanPreference("show_discord_banner", true)
    val showNewBottomSheetBanner = booleanPreference("show_new_bottom_sheet_banner")

    val devBottomSheetExperimentCard = booleanPreference("show_dev_bottom_sheet_experiment_card", true)

    val useDevBottomSheet = booleanPreference("use_dev_bottom_sheet")
    val donateCardDismissed = booleanPreference("donate_card_dismissed")

    val devBottomSheetExperiment = booleanPreference("dev_bottom_sheet_experiment", true)
    val resolveEmbeds = booleanPreference("resolve_embeds")
    val hideBottomSheetChoiceButtons = booleanPreference("hide_bottom_sheet_choice_buttons")


    val sensitivePreferences = listOf(
        useTimeMs, logKey,
    )

    private val sensitivePackagePreferences = listOf(
        selectedBrowser, selectedInAppBrowser
    )

    fun logPackages(
        redact: Boolean,
        logger: Logger,
        repository: AppPreferenceRepository
    ): Map<String, String?> = sensitivePackagePreferences.associate {
        val value = repository.getString(it)
        it.key to if (value != null) {
            logger.dumpParameterToString(redact, value, PackageProcessor)
        } else "<null>"
    }

    fun toJsonArray(preferenceToDumpedValue: Map<String, String?>): JsonArray {
        return jsonArray {
            preferenceToDumpedValue.forEach { (key, value) ->
                +jsonObject {
                    "name" += key
                    "value" += value
                }
            }
        }
    }
}



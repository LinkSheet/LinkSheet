package fe.linksheet.module.preference.app


import app.linksheet.feature.shizuku.preference.shizukuPreferences
import com.google.gson.JsonArray
import fe.android.preference.helper.PreferenceDefinition
import fe.gson.dsl.jsonObject
import fe.gson.util.jsonArrayItems
import fe.linksheet.activity.bottomsheet.TapConfig
import fe.linksheet.module.analytics.TelemetryIdentity
import fe.linksheet.module.analytics.TelemetryLevel
import fe.linksheet.module.preference.SensitivePreference
import fe.linksheet.module.redactor.PackageProcessor
import fe.linksheet.module.redactor.Redactor
import fe.linksheet.module.resolver.InAppBrowserHandler
import fe.linksheet.composable.ui.Theme
import fe.linksheet.composable.ui.ThemeV2
import fe.linksheet.module.resolver.browser.BrowserMode
import io.viascom.nanoid.NanoId
import java.util.*

object AppPreferences : PreferenceDefinition(
    "enable_copy_button",
    "single_tap",
    "enable_send_button",
    "show_new_bottom_sheet_banner",
    "show_dev_bottom_sheet_experiment_card",
    "amp2html_builtin_cache",
    "follow_redirects_builtin_cache",
    "use_text_share_copy_buttons",
    "telemetry_identity",
    "use_dev_bottom_sheet",
    "dev_bottom_sheet_experiment"
) {
    val hideAfterCopying = boolean("hide_after_copying")
    val usageStatsSorting = boolean("usage_stats_sorting")

    val browserMode = mapped(
        "browser_mode",
        BrowserMode.AlwaysAsk,
        BrowserMode
    )

    @SensitivePreference
    val selectedBrowser = string("selected_browser")

    val inAppBrowserMode = mapped(
        "in_app_browser_mode",
        BrowserMode.AlwaysAsk,
        BrowserMode
    )

    @SensitivePreference
    val selectedInAppBrowser = string("selected_in_app_browser")
    val unifiedPreferredBrowser = boolean("unified_preferred_browser", true)

    val inAppBrowserSettings = mapped(
        "in_app_browser_setting",
        InAppBrowserHandler.InAppBrowserMode.UseAppSettings,
        InAppBrowserHandler.InAppBrowserMode
    )

    object Notifications {
        val urlCopiedToast = boolean("url_copied_toast", true)
        val downloadStartedToast = boolean("download_started_toast", true)
        val openingWithAppToast = boolean("opening_with_app_toast", true)
        val resolveViaToast = boolean("resolve_via_toast", true)
        val resolveViaFailedToast = boolean("resolve_via_failed_toast", true)
    }

    val notifications = Notifications

    val alwaysShowPackageName = boolean("always_show_package_name")

    val gridLayout = boolean("grid_layout")
    val useClearUrls = boolean("use_clear_urls")
    val useFastForwardRules = boolean("fast_forward_rules")

    object FollowRedirects {
        val enable = boolean("follow_redirects")
        val localCache = boolean("follow_redirects_local_cache", true)
        val externalService = boolean("follow_redirects_external_service")
        val onlyKnownTrackers = boolean("follow_only_known_trackers")
        val allowDarknets = boolean("follow_redirects_allow_darknets", false)
        val allowLocalNetwork = boolean("follow_redirects_allow_local_network", false)
        val skipBrowser = boolean("follow_redirects_skip_browser", true)
    }

    val followRedirects = FollowRedirects

    val dontShowFilteredItem = boolean("dont_show_filtered_item")

    object Downloader {
        val enable = boolean("enable_downloader")
        val checkUrlMimeType = boolean("downloaderCheckUrlMimeType")
    }

    val downloader = Downloader

    object LibRedirect {
        val enableIgnoreLibRedirectButton = boolean("enable_ignore_lib_redirect_button")
        val enable = boolean("enable_lib_redirect")
    }

    val libRedirect = LibRedirect

    val requestTimeout = int("follow_redirects_timeout", 15)

    object Amp2Html {
        val enable = boolean("enable_amp2html")
        val localCache = boolean("amp2html_local_cache", true)
        val externalService = boolean("amp2html_external_service")
        val allowDarknets = boolean("amp2html_allow_darknets", false)
        val allowLocalNetwork = boolean("amp2html_allow_local_network", false)
        val skipBrowser = boolean("amp2html_skip_browser", true)
    }

    val amp2Html = Amp2Html

    val enableRequestPrivateBrowsingButton = boolean(
        "enable_request_private_browsing_button"
    )

    @SensitivePreference
    val useTimeMs = long("use_time", 0)

    val showLinkSheetAsReferrer = boolean("show_as_referrer")
    val devModeEnabled = boolean("dev_mode_enabled")

    @SensitivePreference
    val logKey = string("log_key") {
        Redactor.createHmacKey()
    }

    val firstRun = boolean("first_run", true)
    val showDiscordBanner = boolean("show_discord_banner", true)

    val donateCardDismissed = boolean("donate_card_dismissed")

    val resolveEmbeds = boolean("resolve_embeds")
    val hideBottomSheetChoiceButtons = boolean("hide_bottom_sheet_choice_buttons")

    @SensitivePreference
    val telemetryId = string("telemetry_id") { NanoId.generate() }

    @SensitivePreference
    val telemetryIdentity = mapped("telemetry_identity_2", TelemetryIdentity.Basic, TelemetryIdentity)

    @SensitivePreference
    val telemetryLevel = mapped("telemetry_level", TelemetryLevel.Standard, TelemetryLevel)
    val telemetryShowInfoDialog = boolean("telemetry_dialog", true)

    val lastVersion = int("last_version", -1)

    object ThemeV2Pref {
        val themeV2 = mapped("theme_v2", ThemeV2.System, ThemeV2)
        val materialYou = boolean("theme_material_you", true)
        val amoled = boolean("theme_amoled_enabled")
    }

    val themeV2 = ThemeV2Pref

    object TapConfigPref {
        val single = mapped("tap_config_single", TapConfig.SelectItem, TapConfig)
        val double = mapped("tap_config_double", TapConfig.OpenApp, TapConfig)
        val long = mapped("tap_config_long", TapConfig.OpenSettings, TapConfig)
    }

    val tapConfig = TapConfigPref

    val expandOnAppSelect = boolean("expand_on_app_select", true)
    val bottomSheetNativeLabel = boolean("bottom_sheet_native_label", true)

    @SensitivePreference
    val installationId = string("installation_id") { UUID.randomUUID().toString() }

    val bottomSheetProfileSwitcher = boolean("bottom_sheet_profile_switcher")

    // TODO: This should be moved to a proper implementation which uses a string set or something similar, but we don't have an API for that (yet)
    val lastVersions = string("last_versions_v0")
//    val lastVersionsV1 = jsonMapped<LastVersion?>("last_versions", null)

    val homeClipboardCard = boolean("home_clipboard_card", true)
    val remoteConfig = boolean("remote_config", false)
    val previewUrl = boolean("preview_url", true)

    object OpenGraphPreview {
        val enable = boolean(key = "url_bar_preview")
        val skipBrowser = boolean(key = "url_bar_preview_skip_browser")
    }

    val openGraphPreview = OpenGraphPreview

    val shizuku = shizukuPreferences(this::boolean)

    init {
        mapped("theme", Theme.System, Theme).migrate { repository, theme ->
            if (!repository.hasStoredValue(themeV2.themeV2)) {
                if (theme == Theme.AmoledBlack) {
                    repository.put(themeV2.amoled, true)
                }

                repository.put(themeV2.themeV2, theme.toV2())
            }
        }

        finalize()
    }

    @SensitivePreference
    val sensitivePreferences = setOf(
        useTimeMs, logKey, telemetryIdentity, telemetryLevel, telemetryId
    )

    @SensitivePreference
    private val sensitivePackagePreferences = setOf(
        selectedBrowser, selectedInAppBrowser
    )

    @OptIn(SensitivePreference::class)
    fun logPackages(redactor: Redactor, repository: AppPreferenceRepository): Map<String, String?> {
        return sensitivePackagePreferences.associate { pref ->
            val value = repository.get(pref)
            val strValue = value?.let { redactor.processToString(it, PackageProcessor) } ?: "<null>"

            pref.key to strValue
        }
    }

    fun toJsonArray(preferences: Map<String, String?>): JsonArray {
        val objs = preferences.map { (key, value) ->
            jsonObject {
                "name" += key
                "value" += value
            }
        }

        return jsonArrayItems(objs)
    }
}



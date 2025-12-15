package fe.linksheet.module.preference.app


import app.linksheet.api.PreferenceRegistry
import app.linksheet.feature.browser.preference.browserPreferences
import app.linksheet.feature.libredirect.preference.libRedirectPreferences
import app.linksheet.feature.profile.preference.profilePreferences
import app.linksheet.feature.shizuku.preference.shizukuPreferences
import com.google.gson.JsonArray
import fe.android.preference.helper.Preference
import fe.android.preference.helper.PreferenceDefinition
import fe.android.preference.helper.TypeMapper
import fe.gson.dsl.jsonObject
import fe.gson.util.jsonArrayItems
import fe.linksheet.composable.ui.Theme
import fe.linksheet.module.analytics.TelemetryIdentity
import fe.linksheet.module.analytics.TelemetryLevel
import fe.linksheet.module.preference.SensitivePreference
import fe.linksheet.module.resolver.InAppBrowserHandler
import fe.linksheet.module.resolver.browser.BrowserMode
import io.viascom.nanoid.NanoId
import java.util.*
import kotlin.reflect.KClass

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
    "dev_bottom_sheet_experiment",
    "show_discord_banner",
    "donate_card_dismissed"
) {
    private val registry = object : PreferenceRegistry {
        override fun boolean(
            key: String,
            default: Boolean
        ): Preference.Boolean {
            return this@AppPreferences.boolean(key, default)
        }

        override fun <T : Any, M : Any> mapped(
            key: String,
            default: T,
            mapper: TypeMapper<T, M>,
            t: KClass<T>,
            m: KClass<M>
        ): Preference.Mapped<T, M> {
            return this@AppPreferences.mapped(key, default, mapper, t, m)
        }
    }

    @SensitivePreference
    val selectedBrowser = string("selected_browser")
    val browserMode = mapped("browser_mode", BrowserMode.AlwaysAsk, BrowserMode)

    @SensitivePreference
    val selectedInAppBrowser = string("selected_in_app_browser")
    val inAppBrowserMode = mapped("in_app_browser_mode", BrowserMode.AlwaysAsk, BrowserMode)

    val unifiedPreferredBrowser = boolean("unified_preferred_browser", true)

    val inAppBrowserSettings = mapped(
        "in_app_browser_setting",
        InAppBrowserHandler.InAppBrowserMode.UseAppSettings,
        InAppBrowserHandler.InAppBrowserMode
    )

    val alwaysShowPackageName = boolean("always_show_package_name")
    val useClearUrls = boolean("use_clear_urls")
    val useFastForwardRules = boolean("fast_forward_rules")
    val requestTimeout = int("follow_redirects_timeout", 15)

    @SensitivePreference
    val useTimeMs = long("use_time", 0)

    val showLinkSheetAsReferrer = boolean("show_as_referrer")
    val devModeEnabled = boolean("dev_mode_enabled")
    val firstRun = boolean("first_run", true)

    val resolveEmbeds = boolean("resolve_embeds")

    @SensitivePreference
    val telemetryId = string("telemetry_id") { NanoId.generate() }
    @SensitivePreference
    val telemetryIdentity = mapped("telemetry_identity_2", TelemetryIdentity.Basic, TelemetryIdentity)
    @SensitivePreference
    val telemetryLevel = mapped("telemetry_level", TelemetryLevel.Standard, TelemetryLevel)
    val telemetryShowInfoDialog = boolean("telemetry_dialog", true)

    val lastVersion = int("last_version", -1)

    @SensitivePreference
    val installationId = string("installation_id") { UUID.randomUUID().toString() }

    // TODO: This should be moved to a proper implementation which uses a string set or something similar, but we don't have an API for that (yet)
    val lastVersions = string("last_versions_v0")
//    val lastVersionsV1 = jsonMapped<LastVersion?>("last_versions", null)

    val homeClipboardCard = boolean("home_clipboard_card", true)
    val remoteConfig = boolean("remote_config", false)
    val previewUrl = boolean("preview_url", true)

    val bottomSheet = BottomSheet(registry)
    val notifications = Notifications(registry)
    val amp2Html = Amp2Html(registry)
    val downloader = Downloader(registry)
    val followRedirects = FollowRedirects(registry)
    val themeV2 = ThemeV2(registry)

    val libRedirect = libRedirectPreferences(registry)
    val shizuku = shizukuPreferences(registry)
    val browser = browserPreferences(registry)
    val profileSwitcher = profilePreferences(registry)

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
    val sensitivePreferences = setOf(useTimeMs, telemetryIdentity, telemetryLevel, telemetryId)

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



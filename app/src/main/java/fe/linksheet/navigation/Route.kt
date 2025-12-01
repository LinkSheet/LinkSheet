package fe.linksheet.navigation


import androidx.annotation.Keep
import androidx.annotation.StringRes
import fe.composekit.route.Route
import fe.linksheet.module.viewmodel.PreferredBrowserViewModel
import fe.linksheet.util.WikiPage
import kotlinx.serialization.Serializable


const val mainRoute = "main_route"
const val settingsRoute = "settings_route"
const val appsSettingsRoute = "apps_settings_route"
const val browserSettingsRoute = "browser_settings_route"

const val inAppBrowserSettingsDisableInSelectedRoute = "inapp_browser_settings_route"

//@Keep
//data class InAppBrowserSettingsDisableInSelectedRoute(
//
//) : RouteData {
//
//}
//
//val inAppBrowserSettingsDisableInSelectedRoute = route(
//    "inapp_browser_settings_route",
//    route = InAppBrowserSettingsDisableInSelectedRoute
//)

//@Keep
//data class WhitelistedBrowserSettingsRoute(
//
//) : RouteData {
//
//}
//
//val whitelistedBrowserSettingsRoute = route(
//    "whitelisted_browser_settings_route",
//    route = WhitelistedBrowserSettingsRoute
//)


const val aboutSettingsRoute = "about_settings_route"
const val creditsSettingsRoute = "credits_settings_route"
const val donateSettingsRoute = "donate_settings_route"

const val themeSettingsRoute = "theme_settings_route"

const val shizukuSettingsRoute = "shizuku_settings_route"


//const val debugSettingsRoute = "debug_settings_route"
const val logViewerSettingsRoute = "log_viewer_settings_route"
const val loadDumpedPreferences = "log_dumped_reference_settings_route"


//val experimentSettingsRoute = ArgumentRoute(
//    "experiment_settings_route",
//    { it -> it.joinToString(separator = "/") { it } },
//    ExperimentSettingsRouteArg.arguments,
//    ExperimentSettingsRouteArg.createInstance,
//    listOf(navDeepLink { uriPattern = "linksheet://experiment/{experiment}" })
//)


const val linksSettingsRoute = "link_settings_route"

const val followRedirectsSettingsRoute = "follow_redirects_settings_route"
const val downloaderSettingsRoute = "downloader_settings_route"
const val amp2HtmlSettingsRoute = "amp2html_settings_route"


const val generalSettingsRoute = "general_settings_route"
const val privacySettingsRoute = "privacy_settings_route"
const val notificationSettingsRoute = "notification_settings_route"
const val bottomSheetSettingsRoute = "bottom_sheet_settings_route"

@Keep
@Serializable
data object PreferredBrowserSettingsRoute : Route

@Keep
@Serializable
data class WhitelistedBrowsersSettingsRoute(val type: PreferredBrowserViewModel.BrowserType) : Route

@Keep
@Serializable
data class SingleBrowserSettingsRoute(val type: PreferredBrowserViewModel.BrowserType) : Route

const val inAppBrowserSettingsRoute = "in_app_browser_settings_route"
const val pretendToBeAppRoute = "pretend_to_be_app"
const val devModeRoute = "dev_mode"

@Keep
@Serializable
data object AppsWhichCanOpenLinksSettingsRoute : Route

@Keep
@Serializable
data class MarkdownViewerRoute(
    val title: String,
    val url: String,
    val rawUrl: String = url,
    @param:StringRes val customTitle: Int? = null,
) : Route {
    constructor(wikiPage: WikiPage) : this(wikiPage.page, wikiPage.url, wikiPage.rawUrl, wikiPage.customTitle)
}

@Keep
@Serializable
data class ExperimentRoute(
    val experiment: String? = null,
) : Route {
    companion object {
        val Empty = ExperimentRoute()
    }
}

@Keep
@Serializable
data object ExportImportRoute : Route

@Keep
@Serializable
data object AdvancedRoute : Route

@Keep
@Serializable
data object DebugRoute : Route

@Keep
@Serializable
data class LogTextViewerRoute(val id: String?, val name: String) : Route

@Keep
@Serializable
data class VlhAppRoute(val packageName: String) : Route

@Keep
@Serializable
data object PreviewUrlRoute : Route

@Keep
@Serializable
data object LanguageRoute : Route

@Keep
@Serializable
data object SqlRoute : Route

//@Keep
//data class ExperimentSettingsRouteArg(val experiment: String?) : RouteData {
//    companion object : Route1<ExperimentSettingsRouteArg, String?>(
//        Argument(ExperimentSettingsRouteArg::experiment),
//        ::ExperimentSettingsRouteArg
//    )
//}


object Routes {
    const val Help = "route__help"
    const val Shortcuts = "route__shortcuts"
    const val Updates = "route__updates"
    const val RuleOverview = "route__rules_overview"
    const val RuleNew = "route__rule_new"
    const val AboutVersion = "route__about_version"
    const val ProfileSwitching = "route__profile_switching"
}


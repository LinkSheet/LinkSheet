package fe.linksheet


import fe.linksheet.util.Route1
import fe.linksheet.util.RouteData
import fe.linksheet.util.route


const val mainRoute = "main_route"
const val settingsRoute = "settings_route"
const val appsSettingsRoute = "apps_settings_route"
const val browserSettingsRoute = "browser_settings_route"
const val aboutSettingsRoute = "about_settings_route"
const val creditsSettingsRoute = "credits_settings_route"

const val themeSettingsRoute = "theme_settings_route"
const val linksSettingsRoute = "link_settings_route"
const val libRedirectSettingsRoute = "lib_redirect_settings_route"

data class LibRedirectServiceRoute(val serviceKey: String) : RouteData {
    companion object : Route1<LibRedirectServiceRoute, String>(
        Argument(LibRedirectServiceRoute::serviceKey, ""),
        { LibRedirectServiceRoute(it) }
    )
}

val libRedirectServiceSettingsRoute = route(
    "lib_redirect_service_settings_route",
    route = LibRedirectServiceRoute
)

const val followRedirectsSettingsRoute = "follow_redirects_settings_route"

const val bottomSheetSettingsRoute = "bottom_sheet_settings_route"
const val preferredBrowserSettingsRoute = "preferred_browser_settings_route"
const val inAppBrowserSettingsRoute = "in_app_browser_settings_route"
const val preferredAppsSettingsRoute = "preferred_apps_settings_route"
const val appsWhichCanOpenLinksSettingsRoute = "apps_which_can_open_links_settings_route"
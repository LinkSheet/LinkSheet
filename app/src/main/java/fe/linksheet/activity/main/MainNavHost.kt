package fe.linksheet.activity.main

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import app.linksheet.compose.util.animatedComposable
import app.linksheet.feature.browser.navigation.PrivateBrowsingNavSubGraph
import app.linksheet.feature.engine.navigation.ScenarioNavSubGraph
import app.linksheet.feature.libredirect.navigation.LibRedirectNavSubGraph
import app.linksheet.feature.shizuku.navigation.ShizukuNavSubGraph
import app.linksheet.feature.wiki.navigation.WikiNavSubGraph
import fe.composekit.core.AndroidVersion
import fe.composekit.route.NavTypes
import fe.composekit.route.Route
import fe.composekit.route.attachSubGraph
import fe.linksheet.composable.page.home.HomePageNavSubGraph
import fe.linksheet.composable.page.home.MainOverviewRoute
import fe.linksheet.composable.page.settings.SettingsRoute
import fe.linksheet.composable.page.settings.about.AboutSettingsRoute
import fe.linksheet.composable.page.settings.about.CreditsSettingsRoute
import fe.linksheet.composable.page.settings.about.VersionSettingsRoute
import fe.linksheet.composable.page.settings.advanced.AdvancedSettingsRoute
import fe.linksheet.composable.page.settings.advanced.ExperimentsSettingsRoute
import fe.linksheet.composable.page.settings.advanced.ExportImportSettingsRoute
import fe.linksheet.composable.page.settings.app.RuleOverviewRoute
import fe.linksheet.composable.page.settings.app.RuleRoute
import fe.linksheet.composable.page.settings.apps.verifiedlinkhandlers.VerifiedLinkHandlersRoute
import fe.linksheet.composable.page.settings.apps.verifiedlinkhandlers.VlhAppRoute
import fe.linksheet.composable.page.settings.bottomsheet.BottomSheetSettingsRoute
import fe.linksheet.composable.page.settings.bottomsheet.ProfileSwitchingSettingsRoute
import fe.linksheet.composable.page.settings.browser.BrowserSettingsRoute
import fe.linksheet.composable.page.settings.browser.inapp.InAppBrowserSettingsDisableInSelectedRoute
import fe.linksheet.composable.page.settings.browser.inapp.InAppBrowserSettingsRoute
import fe.linksheet.composable.page.settings.browser.mode.PreferredBrowserSettingsRoute
import fe.linksheet.composable.page.settings.browser.mode.SingleBrowserSettingsRoute
import fe.linksheet.composable.page.settings.browser.mode.WhitelistedBrowsersSettingsRoute
import fe.linksheet.composable.page.settings.debug.DebugSettingsRoute
import fe.linksheet.composable.page.settings.debug.SqlRoute
import fe.linksheet.composable.page.settings.debug.loadpreferences.LoadDumpedPreferences
import fe.linksheet.composable.page.settings.debug.log.LogSettingsRoute
import fe.linksheet.composable.page.settings.debug.log.LogTextSettingsRoute
import fe.linksheet.composable.page.settings.dev.DevSettingsRoute
import fe.linksheet.composable.page.settings.link.LinksSettingsRoute
import fe.linksheet.composable.page.settings.link.amp2html.Amp2HtmlSettingsRoute
import fe.linksheet.composable.page.settings.link.downloader.DownloaderSettingsRoute
import fe.linksheet.composable.page.settings.link.preview.PreviewSettingsRoute
import fe.linksheet.composable.page.settings.link.redirect.FollowRedirectsSettingsRoute
import fe.linksheet.composable.page.settings.misc.MiscSettingsRoute
import fe.linksheet.composable.page.settings.notification.NotificationSettingsRoute
import fe.linksheet.composable.page.settings.privacy.PrivacySettingsRoute
import fe.linksheet.composable.page.settings.shortcuts.ShortcutsRoute
import fe.linksheet.composable.page.settings.theme.ThemeSettingsRoute
import fe.linksheet.navigation.*

@Composable
fun MainNavHost(
    // TODO: Refactor navController away
    navController: NavHostController,
    navigate: (String) -> Unit,
    navigateNew: (Route) -> Unit,
    onBackPressed: () -> Unit,
) {
    NavHost(
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
        navController = navController,
        typeMap = NavTypes.Types,
        startDestination = MainOverviewRoute
    ) {
        attachSubGraph(HomePageNavSubGraph, navController)
        attachSubGraph(ScenarioNavSubGraph, navController)
        attachSubGraph(ShizukuNavSubGraph, navController)
        attachSubGraph(LibRedirectNavSubGraph, navController)
        attachSubGraph(PrivateBrowsingNavSubGraph, navController)
        attachSubGraph(WikiNavSubGraph, navController)

        animatedComposable<ExperimentRoute> { _, route ->
            ExperimentsSettingsRoute(onBackPressed = onBackPressed, experiment = route.experiment)
        }

        animatedComposable<ExportImportRoute> { _, _ ->
            ExportImportSettingsRoute(onBackPressed = onBackPressed)
        }

        animatedComposable<AdvancedRoute> { _, _ ->
            AdvancedSettingsRoute(onBackPressed = onBackPressed, navigate = navigateNew)
        }

        animatedComposable<DebugRoute> { _, _ ->
            DebugSettingsRoute(onBackPressed = onBackPressed, navigate = navigate)
        }

        animatedComposable<LogTextViewerRoute> { _, route ->
            LogTextSettingsRoute(onBackPressed = onBackPressed, sessionId = route.id, sessionName = route.name)
        }

        animatedComposable<VlhAppRoute> { _, route ->
            VlhAppRoute(onBackPressed = onBackPressed, packageName = route.packageName)
        }

        animatedComposable<SqlRoute> { _, route ->
            SqlRoute(onBackPressed = onBackPressed)
        }

        animatedComposable<PreviewUrlRoute> { _, route ->
            PreviewSettingsRoute(onBackPressed = onBackPressed)
        }

        animatedComposable(route = Routes.AboutVersion) {
            VersionSettingsRoute(onBackPressed = onBackPressed, navigate = navigate)
        }

        animatedComposable(route = Routes.RuleOverview) {
            RuleOverviewRoute(onBackPressed = onBackPressed)
        }

        animatedComposable(route = Routes.RuleNew) {
            RuleRoute(onBackPressed = onBackPressed)
        }

        animatedComposable(route = settingsRoute) {
            SettingsRoute(onBackPressed = onBackPressed, navigate = navigate, navigateNew = navigateNew)
        }

        animatedComposable(route = browserSettingsRoute) {
            BrowserSettingsRoute(onBackPressed = onBackPressed, navigate = navigate, navigateNew = navigateNew)
        }

        animatedComposable(route = generalSettingsRoute) {
            MiscSettingsRoute(onBackPressed = onBackPressed)
        }

        animatedComposable(route = notificationSettingsRoute) {
            NotificationSettingsRoute(onBackPressed = onBackPressed)
        }

        animatedComposable(route = privacySettingsRoute) {
            PrivacySettingsRoute(onBackPressed = onBackPressed)
        }

        animatedComposable(route = bottomSheetSettingsRoute) {
            BottomSheetSettingsRoute(
                onBackPressed = onBackPressed,
                navigate = navigate,
                navigateNew = navigateNew
            )
        }

        if (AndroidVersion.isAtLeastApi30R()) {
            animatedComposable(route = Routes.ProfileSwitching) {
                ProfileSwitchingSettingsRoute(onBackPressed = onBackPressed)
            }
        }

        animatedComposable(route = linksSettingsRoute) {
            LinksSettingsRoute(
                onBackPressed = onBackPressed,
                navigate = navigate,
                navigateNew = navigateNew
            )
        }

        animatedComposable(route = followRedirectsSettingsRoute) {
            FollowRedirectsSettingsRoute(onBackPressed = onBackPressed)
        }

        animatedComposable(route = downloaderSettingsRoute) {
            DownloaderSettingsRoute(onBackPressed = onBackPressed)
        }

        animatedComposable(route = amp2HtmlSettingsRoute) {
            Amp2HtmlSettingsRoute(onBackPressed = onBackPressed)
        }

        animatedComposable(route = themeSettingsRoute) {
            ThemeSettingsRoute(onBackPressed = onBackPressed)
        }

        animatedComposable(route = logViewerSettingsRoute) {
            LogSettingsRoute(onBackPressed = onBackPressed, navigate = navigateNew)
        }

        animatedComposable(route = loadDumpedPreferences) {
            LoadDumpedPreferences(onBackPressed = onBackPressed)
        }

        animatedComposable(route = aboutSettingsRoute) {
            AboutSettingsRoute(
                navigate = navigate, onBackPressed = onBackPressed
            )
        }

        animatedComposable(route = creditsSettingsRoute) {
            CreditsSettingsRoute(onBackPressed = onBackPressed)
        }
        animatedComposable<PreferredBrowserSettingsRoute> { _, _ ->
            PreferredBrowserSettingsRoute(
                onBackPressed = onBackPressed,
                navigate = navigateNew,
            )
        }
        animatedComposable<WhitelistedBrowsersSettingsRoute> { _, route ->
            WhitelistedBrowsersSettingsRoute(
                type = route.type,
                onBackPressed = onBackPressed,
            )
        }
        animatedComposable<SingleBrowserSettingsRoute> { _, route ->
            SingleBrowserSettingsRoute(
                type = route.type,
                onBackPressed = onBackPressed,
            )
        }

        animatedComposable(route = inAppBrowserSettingsRoute) {
            InAppBrowserSettingsRoute(onBackPressed = onBackPressed, navigate = navigate)
        }

        animatedComposable(route = inAppBrowserSettingsDisableInSelectedRoute) {
            InAppBrowserSettingsDisableInSelectedRoute(onBackPressed = onBackPressed)
        }

//        animatedComposable(route = preferredAppsSettingsRoute) {
//            PreferredAppSettingsRoute(onBackPressed = onBackPressed)
//        }

        animatedComposable<AppsWhichCanOpenLinksSettingsRoute> { _, _ ->
            VerifiedLinkHandlersRoute(onBackPressed = onBackPressed, navigateNew = navigateNew)
        }

        animatedComposable(route = devModeRoute) {
            DevSettingsRoute(onBackPressed = onBackPressed)
        }

        animatedComposable(route = Routes.Help) {
//            DevBottomSheetSettingsRoute(onBackPressed = onBackPressed)
        }

        animatedComposable(route = Routes.Shortcuts) {
            ShortcutsRoute(onBackPressed = onBackPressed, navigate = navigate)
        }

        animatedComposable(route = Routes.Updates) {
//            DevBottomSheetSettingsRoute(onBackPressed = onBackPressed)
        }
    }
}

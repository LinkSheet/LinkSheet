package fe.linksheet.activity.main

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import fe.linksheet.composable.page.home.HomePageRoute
import fe.linksheet.composable.page.settings.NewSettingsRoute
import fe.linksheet.composable.page.settings.about.DonateSettingsRoute
import fe.linksheet.composable.page.settings.about.NewAboutSettingsRoute
import fe.linksheet.composable.page.settings.about.NewCreditsSettingsRoute
import fe.linksheet.composable.page.settings.about.VersionSettingsRoute
import fe.linksheet.composable.page.settings.advanced.*
import fe.linksheet.composable.page.settings.app.NewRuleRoute
import fe.linksheet.composable.page.settings.app.RuleOverviewRoute
import fe.linksheet.composable.page.settings.apps.verifiedlinkhandlers.VerifiedLinkHandlersRoute
import fe.linksheet.composable.page.settings.apps.AppsSettingsRoute
import fe.linksheet.composable.page.settings.apps.PretendToBeAppSettingsRoute
import fe.linksheet.composable.page.settings.bottomsheet.BottomSheetSettingsRoute
import fe.linksheet.composable.page.settings.bottomsheet.ProfileSwitchingSettingsRoute
import fe.linksheet.composable.page.settings.browser.NewBrowserSettingsRoute
import fe.linksheet.composable.page.settings.browser.inapp.NewInAppBrowserSettingsDisableInSelectedRoute
import fe.linksheet.composable.page.settings.browser.inapp.NewInAppBrowserSettingsRoute
import fe.linksheet.composable.page.settings.browser.mode.PreferredBrowserSettingsRoute
import fe.linksheet.composable.page.settings.browser.mode.WhitelistedBrowsersSettingsRoute
import fe.linksheet.composable.page.settings.debug.NewDebugSettingsRoute
import fe.linksheet.composable.page.settings.debug.loadpreferences.LoadDumpedPreferences
import fe.linksheet.composable.page.settings.debug.log.NewLogSettingsRoute
import fe.linksheet.composable.page.settings.debug.log.NewLogTextSettingsRoute
import fe.linksheet.composable.page.settings.dev.DevSettingsRoute
import fe.linksheet.composable.page.settings.link.NewLinksSettingsRoute
import fe.linksheet.composable.page.settings.link.amp2html.NewAmp2HtmlSettingsRoute
import fe.linksheet.composable.page.settings.link.downloader.NewDownloaderSettingsRoute
import fe.linksheet.composable.page.settings.link.libredirect.NewLibRedirectServiceSettingsRoute
import fe.linksheet.composable.page.settings.link.libredirect.NewLibRedirectSettingsRoute
import fe.linksheet.composable.page.settings.link.redirect.NewFollowRedirectsSettingsRoute
import fe.linksheet.composable.page.settings.misc.MiscSettingsRoute
import fe.linksheet.composable.page.settings.notification.NewNotificationSettingsRoute
import fe.linksheet.composable.page.settings.privacy.PrivacySettingsRoute
import fe.linksheet.composable.page.settings.shortcuts.ShortcutsRoute
import fe.linksheet.composable.page.settings.theme.ThemeSettingsRoute
import fe.linksheet.composable.util.*
import fe.linksheet.navigation.addPageRoute
import fe.composekit.core.AndroidVersion
import fe.composekit.route.Route
import fe.linksheet.composable.page.mdviewer.MarkdownViewerWrapper
import fe.linksheet.composable.page.settings.apps.verifiedlinkhandlers.VlhAppRoute
import fe.linksheet.composable.page.settings.debug.SqlRoute
import fe.linksheet.navigation.AdvancedRoute
import fe.linksheet.navigation.DebugRoute
import fe.linksheet.navigation.ExperimentRoute
import fe.linksheet.navigation.ExportImportRoute
import fe.linksheet.navigation.LibRedirectRoute
import fe.linksheet.navigation.LibRedirectServiceRoute
import fe.linksheet.navigation.LogTextViewerRoute
import fe.linksheet.navigation.MarkdownViewerRoute
import fe.linksheet.navigation.Routes
import fe.linksheet.navigation.SqlRoute
import fe.linksheet.navigation.VlhAppRoute
import fe.linksheet.navigation.aboutSettingsRoute
import fe.linksheet.navigation.amp2HtmlSettingsRoute
import fe.linksheet.navigation.appsSettingsRoute
import fe.linksheet.navigation.appsWhichCanOpenLinksSettingsRoute
import fe.linksheet.navigation.bottomSheetSettingsRoute
import fe.linksheet.navigation.browserSettingsRoute
import fe.linksheet.navigation.creditsSettingsRoute
import fe.linksheet.navigation.devModeRoute
import fe.linksheet.navigation.donateSettingsRoute
import fe.linksheet.navigation.downloaderSettingsRoute
import fe.linksheet.navigation.followRedirectsSettingsRoute
import fe.linksheet.navigation.generalSettingsRoute
import fe.linksheet.navigation.inAppBrowserSettingsDisableInSelectedRoute
import fe.linksheet.navigation.inAppBrowserSettingsRoute
import fe.linksheet.navigation.linksSettingsRoute
import fe.linksheet.navigation.loadDumpedPreferences
import fe.linksheet.navigation.logViewerSettingsRoute
import fe.linksheet.navigation.notificationSettingsRoute
import fe.linksheet.navigation.preferredBrowserSettingsRoute
import fe.linksheet.navigation.pretendToBeAppRoute
import fe.linksheet.navigation.privacySettingsRoute
import fe.linksheet.navigation.settingsRoute
import fe.linksheet.navigation.shizukuSettingsRoute
import fe.linksheet.navigation.themeSettingsRoute
import fe.linksheet.navigation.whitelistedBrowsersSettingsRoute

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
        startDestination = HomePageRoute
    ) {
        addPageRoute(HomePageRoute, navController)

        animatedComposable<MarkdownViewerRoute> { _, route ->
            val titleStr = route.customTitle?.let { stringResource(id = it) } ?: route.title
            MarkdownViewerWrapper(
                title = titleStr,
                url = route.url,
                rawUrl = route.rawUrl,
                onBackPressed = onBackPressed
            )
        }

        animatedComposable<ExperimentRoute> { _, route ->
            NewExperimentsSettingsRoute(onBackPressed = onBackPressed, experiment = route.experiment)
        }

        animatedComposable<ExportImportRoute> { _, _ ->
            ExportImportSettingsRoute(onBackPressed = onBackPressed)
        }

        animatedComposable<AdvancedRoute> { _, _ ->
            NewAdvancedSettingsRoute(onBackPressed = onBackPressed, navigate = navigateNew)
        }

        animatedComposable<DebugRoute> { _, _ ->
            NewDebugSettingsRoute(onBackPressed = onBackPressed, navigate = navigate)
        }

        animatedComposable<LogTextViewerRoute> { _, route ->
            NewLogTextSettingsRoute(onBackPressed = onBackPressed, sessionId = route.id, sessionName = route.name)
        }

        animatedComposable<LibRedirectRoute> { _, route ->
            NewLibRedirectSettingsRoute(
                onBackPressed = onBackPressed,
                navigate = navigateNew,
            )
        }

        animatedComposable<LibRedirectServiceRoute> { _, route ->
            NewLibRedirectServiceSettingsRoute(onBackPressed = onBackPressed, serviceKey = route.serviceKey)
        }

        animatedComposable<VlhAppRoute> { _, route ->
            VlhAppRoute(onBackPressed = onBackPressed, packageName = route.packageName)
        }

        animatedComposable<SqlRoute> { _, route ->
            SqlRoute(onBackPressed = onBackPressed)
        }

        animatedComposable(route = Routes.AboutVersion) {
            VersionSettingsRoute(onBackPressed = onBackPressed, navigate = navigate)
        }

        animatedComposable(route = Routes.RuleOverview) {
            RuleOverviewRoute(onBackPressed = onBackPressed)
        }

        animatedComposable(route = Routes.RuleNew) {
            NewRuleRoute(onBackPressed = onBackPressed)
        }

        animatedComposable(route = settingsRoute) {
            NewSettingsRoute(onBackPressed = onBackPressed, navigate = navigate, navigateNew = navigateNew)
        }

        animatedComposable(route = appsSettingsRoute) {
            AppsSettingsRoute(
                navController = navController, onBackPressed = onBackPressed
            )
        }

        animatedComposable(route = browserSettingsRoute) {
            NewBrowserSettingsRoute(onBackPressed = onBackPressed, navigate = navigate)
        }

        animatedComposable(route = generalSettingsRoute) {
            MiscSettingsRoute(onBackPressed = onBackPressed)
        }

        animatedComposable(route = notificationSettingsRoute) {
            NewNotificationSettingsRoute(onBackPressed = onBackPressed)
        }

        animatedComposable(route = privacySettingsRoute) {
            PrivacySettingsRoute(onBackPressed = onBackPressed)
        }

        animatedComposable(route = bottomSheetSettingsRoute) {
            BottomSheetSettingsRoute(onBackPressed = onBackPressed, navigate = navigate)
        }

        if (AndroidVersion.isAtLeastApi30R()) {
            animatedComposable(route = Routes.ProfileSwitching) {
                ProfileSwitchingSettingsRoute(onBackPressed = onBackPressed)
            }
        }

        animatedComposable(route = linksSettingsRoute) {
            NewLinksSettingsRoute(
                onBackPressed = onBackPressed,
                navigate = navigate,
                navigateNew = navigateNew
            )
        }

        animatedComposable(route = followRedirectsSettingsRoute) {
            NewFollowRedirectsSettingsRoute(onBackPressed = onBackPressed)
        }

        animatedComposable(route = downloaderSettingsRoute) {
            NewDownloaderSettingsRoute(onBackPressed = onBackPressed)
        }

        animatedComposable(route = amp2HtmlSettingsRoute) {
            NewAmp2HtmlSettingsRoute(onBackPressed = onBackPressed)
        }

        animatedComposable(route = themeSettingsRoute) {
            ThemeSettingsRoute(onBackPressed = onBackPressed)
        }

        animatedComposable(route = shizukuSettingsRoute) {
            ShizukuSettingsRoute(
                navController = navController, onBackPressed = onBackPressed
            )
        }

        animatedComposable(route = logViewerSettingsRoute) {
            NewLogSettingsRoute(onBackPressed = onBackPressed, navigate = navigateNew)
        }

        animatedComposable(route = loadDumpedPreferences) {
            LoadDumpedPreferences(onBackPressed = onBackPressed)
        }

        animatedComposable(route = aboutSettingsRoute) {
            NewAboutSettingsRoute(
                navigate = navigate, onBackPressed = onBackPressed
            )
        }

        animatedComposable(route = donateSettingsRoute) {
            DonateSettingsRoute(
                onBackPressed = onBackPressed
            )
        }

        animatedComposable(route = creditsSettingsRoute) {
            NewCreditsSettingsRoute(onBackPressed = onBackPressed)
        }

        animatedComposable(route = preferredBrowserSettingsRoute) {
            PreferredBrowserSettingsRoute(
                navController = navController, onBackPressed = onBackPressed
            )
        }

        animatedComposable(route = whitelistedBrowsersSettingsRoute) {
            WhitelistedBrowsersSettingsRoute(navController = navController)
        }

        animatedComposable(route = inAppBrowserSettingsRoute) {
            NewInAppBrowserSettingsRoute(onBackPressed = onBackPressed, navigate = navigate)
        }

        animatedComposable(route = inAppBrowserSettingsDisableInSelectedRoute) {
            NewInAppBrowserSettingsDisableInSelectedRoute(onBackPressed = onBackPressed)
        }

//        animatedComposable(route = preferredAppsSettingsRoute) {
//            PreferredAppSettingsRoute(onBackPressed = onBackPressed)
//        }

        animatedComposable(route = appsWhichCanOpenLinksSettingsRoute) {
            VerifiedLinkHandlersRoute(onBackPressed = onBackPressed, navigateNew = navigateNew)
        }

        if (AndroidVersion.isAtLeastApi31S()) {
            animatedComposable(route = pretendToBeAppRoute) {
                PretendToBeAppSettingsRoute(onBackPressed = onBackPressed)
            }
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

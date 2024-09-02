package fe.linksheet.activity.main

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import fe.linksheet.*
import fe.linksheet.composable.page.settings.about.DonateSettingsRoute
import fe.linksheet.composable.page.settings.advanced.ExportImportSettingsRoute
import fe.linksheet.composable.page.settings.advanced.FeatureFlagSettingsRoute
import fe.linksheet.composable.page.settings.advanced.ShizukuSettingsRoute
import fe.linksheet.composable.page.settings.apps.AppsSettingsRoute
import fe.linksheet.composable.page.settings.apps.PretendToBeAppSettingsRoute
import fe.linksheet.composable.page.settings.apps.preferred.PreferredAppSettingsRoute
import fe.linksheet.composable.page.settings.bottomsheet.BottomSheetSettingsRoute
import fe.linksheet.composable.page.settings.browser.mode.PreferredBrowserSettingsRoute
import fe.linksheet.composable.page.settings.browser.mode.WhitelistedBrowsersSettingsRoute
import fe.linksheet.composable.page.settings.debug.loadpreferences.LoadDumpedPreferences
import fe.linksheet.composable.page.settings.dev.DevSettingsRoute
import fe.linksheet.composable.page.settings.privacy.PrivacySettingsRoute
import fe.linksheet.composable.page.settings.theme.ThemeSettingsRoute
import fe.linksheet.composable.util.animatedArgumentRouteComposable
import fe.linksheet.composable.util.animatedComposable
import fe.linksheet.composable.page.main.NewMainRoute
import fe.linksheet.composable.page.settings.NewSettingsRoute
import fe.linksheet.composable.page.settings.about.NewAboutSettingsRoute
import fe.linksheet.composable.page.settings.about.NewCreditsSettingsRoute
import fe.linksheet.composable.page.settings.about.VersionSettingsRoute
import fe.linksheet.composable.page.settings.advanced.NewAdvancedSettingsRoute
import fe.linksheet.composable.page.settings.advanced.NewExperimentsSettingsRoute
import fe.linksheet.composable.page.settings.app.NewRuleRoute
import fe.linksheet.composable.page.settings.app.RuleOverviewRoute
import fe.linksheet.composable.page.settings.app.VerifiedLinkHandlersRoute
import fe.linksheet.composable.page.settings.browser.NewBrowserSettingsRoute
import fe.linksheet.composable.page.settings.browser.inapp.NewInAppBrowserSettingsDisableInSelectedRoute
import fe.linksheet.composable.page.settings.browser.inapp.NewInAppBrowserSettingsRoute
import fe.linksheet.composable.page.settings.debug.NewDebugSettingsRoute
import fe.linksheet.composable.page.settings.debug.log.NewLogSettingsRoute
import fe.linksheet.composable.page.settings.debug.log.NewLogTextSettingsRoute
import fe.linksheet.composable.page.settings.link.NewLinksSettingsRoute
import fe.linksheet.composable.page.settings.link.amp2html.NewAmp2HtmlSettingsRoute
import fe.linksheet.composable.page.settings.link.downloader.NewDownloaderSettingsRoute
import fe.linksheet.composable.page.settings.link.libredirect.NewLibRedirectServiceSettingsRoute
import fe.linksheet.composable.page.settings.link.libredirect.NewLibRedirectSettingsRoute
import fe.linksheet.composable.page.settings.link.redirect.NewFollowRedirectsSettingsRoute
import fe.linksheet.composable.page.settings.misc.MiscSettingsRoute
import fe.linksheet.composable.page.settings.notification.NewNotificationSettingsRoute
import fe.linksheet.composable.page.settings.shortcuts.ShortcutsRoute
import fe.linksheet.util.AndroidVersion

@Composable
fun MainNavHost(
    // TODO: Refactor navController away
    navController: NavHostController,
    navigate: (String) -> Unit,
    onBackPressed: () -> Unit,
) {
    NavHost(
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
        navController = navController,
        startDestination = mainRoute
    ) {
        animatedComposable(route = mainRoute) {
//            NewRuleRoute(onBackPressed = onBackPressed)
//            EditRuleRoute(onBackPressed = onBackPressed)
            NewMainRoute(navController = navController)
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
            NewSettingsRoute(onBackPressed = onBackPressed, navigate = navigate)
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
            BottomSheetSettingsRoute(onBackPressed = onBackPressed)
        }

        animatedComposable(route = linksSettingsRoute) {
            NewLinksSettingsRoute(
                onBackPressed = onBackPressed,
                navigate = navigate
            )
        }

        animatedComposable(route = followRedirectsSettingsRoute) {
            NewFollowRedirectsSettingsRoute(onBackPressed = onBackPressed)
        }

        animatedComposable(route = libRedirectSettingsRoute) {
            NewLibRedirectSettingsRoute(onBackPressed = onBackPressed, navController = navController)
        }

        animatedArgumentRouteComposable(route = libRedirectServiceSettingsRoute) { _, _ ->
            NewLibRedirectServiceSettingsRoute(onBackPressed = onBackPressed)
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

        animatedComposable(route = advancedSettingsRoute) {
            NewAdvancedSettingsRoute(onBackPressed = onBackPressed, navigate = navigate)
        }

        animatedComposable(route = shizukuSettingsRoute) {
            ShizukuSettingsRoute(
                navController = navController, onBackPressed = onBackPressed
            )
        }

        animatedComposable(route = featureFlagSettingsRoute) {
            FeatureFlagSettingsRoute(
                navController = navController, onBackPressed = onBackPressed
            )
        }

        animatedArgumentRouteComposable(route = experimentSettingsRoute) { _, _ ->
            NewExperimentsSettingsRoute(onBackPressed = onBackPressed)
        }

        animatedComposable(route = exportImportSettingsRoute) {
            ExportImportSettingsRoute(onBackPressed = onBackPressed)
        }

        animatedComposable(route = debugSettingsRoute) {
            NewDebugSettingsRoute(onBackPressed = onBackPressed, navigate = navigate)
        }

        animatedComposable(route = logViewerSettingsRoute) {
            NewLogSettingsRoute(onBackPressed = onBackPressed, navigate = navigate)
        }

        animatedComposable(route = loadDumpedPreferences) {
            LoadDumpedPreferences(onBackPressed = onBackPressed)
        }

        animatedArgumentRouteComposable(route = logTextViewerSettingsRoute) { _, _ ->
            NewLogTextSettingsRoute(onBackPressed = onBackPressed)
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

        animatedComposable(route = preferredAppsSettingsRoute) {
            PreferredAppSettingsRoute(onBackPressed = onBackPressed)
        }

        if (AndroidVersion.AT_LEAST_API_31_S) {
            animatedComposable(route = appsWhichCanOpenLinksSettingsRoute) {
                VerifiedLinkHandlersRoute(onBackPressed = onBackPressed)
            }

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

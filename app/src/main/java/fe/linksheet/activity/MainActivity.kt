package fe.linksheet.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fe.android.compose.route.util.composable
import fe.linksheet.aboutSettingsRoute
import fe.linksheet.appsSettingsRoute
import fe.linksheet.appsWhichCanOpenLinksSettingsRoute
import fe.linksheet.bottomSheetSettingsRoute
import fe.linksheet.browserSettingsRoute
import fe.linksheet.composable.main.MainRoute
import fe.linksheet.composable.settings.SettingsRoute
import fe.linksheet.composable.settings.about.AboutSettingsRoute
import fe.linksheet.composable.settings.about.CreditsSettingsRoute
import fe.linksheet.composable.settings.apps.AppsSettingsRoute
import fe.linksheet.composable.settings.apps.link.AppsWhichCanOpenLinksSettingsRoute
import fe.linksheet.composable.settings.apps.preferred.PreferredAppSettingsRoute
import fe.linksheet.composable.settings.bottomsheet.BottomSheetSettingsRoute
import fe.linksheet.composable.settings.browser.BrowserSettingsRoute
import fe.linksheet.composable.settings.browser.inapp.InAppBrowserSettingsRoute
import fe.linksheet.composable.settings.browser.mode.PreferredBrowserSettingsRoute
import fe.linksheet.composable.settings.debug.DebugSettingsRoute
import fe.linksheet.composable.settings.debug.log.LogSettingsRoute
import fe.linksheet.composable.settings.debug.log.LogTextSettingsRoute
import fe.linksheet.composable.settings.link.LinksSettingsRoute
import fe.linksheet.composable.settings.link.downloader.DownloaderSettingsRoute
import fe.linksheet.composable.settings.link.libredirect.LibRedirectServiceSettingsRoute
import fe.linksheet.composable.settings.link.libredirect.LibRedirectSettingsRoute
import fe.linksheet.composable.settings.link.redirect.FollowRedirectsSettingsRoute
import fe.linksheet.composable.settings.theme.ThemeSettingsRoute
import fe.linksheet.creditsSettingsRoute
import fe.linksheet.debugSettingsRoute
import fe.linksheet.downloaderSettingsRoute
import fe.linksheet.followRedirectsSettingsRoute
import fe.linksheet.inAppBrowserSettingsRoute
import fe.linksheet.libRedirectServiceSettingsRoute
import fe.linksheet.libRedirectSettingsRoute
import fe.linksheet.linksSettingsRoute
import fe.linksheet.logTextViewerSettingsRoute
import fe.linksheet.logViewerSettingsRoute
import fe.linksheet.mainRoute
import fe.linksheet.preferredAppsSettingsRoute
import fe.linksheet.preferredBrowserSettingsRoute
import fe.linksheet.settingsRoute
import fe.linksheet.themeSettingsRoute
import fe.linksheet.ui.AppHost
import fe.linksheet.util.AndroidVersion

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            AppHost {
                Spacer(modifier = Modifier.height(5.dp))

                NavHost(
                    navController = navController,
                    startDestination = mainRoute,
                ) {
                    composable(route = mainRoute) {
                        MainRoute(navController = navController)
                    }

                    val onBackPressed: () -> Unit = { navController.popBackStack() }
                    composable(route = settingsRoute) {
                        SettingsRoute(
                            navController = navController,
                            onBackPressed = onBackPressed
                        )
                    }

                    composable(route = appsSettingsRoute) {
                        AppsSettingsRoute(
                            navController = navController,
                            onBackPressed = onBackPressed
                        )
                    }

                    composable(route = browserSettingsRoute) {
                        BrowserSettingsRoute(
                            navController = navController,
                            onBackPressed = onBackPressed
                        )
                    }

                    composable(route = bottomSheetSettingsRoute) {
                        BottomSheetSettingsRoute(
                            onBackPressed = onBackPressed,
                        )
                    }

                    composable(route = linksSettingsRoute) {
                        LinksSettingsRoute(
                            onBackPressed = onBackPressed,
                            navController = navController,
                        )
                    }

                    composable(route = followRedirectsSettingsRoute) {
                        FollowRedirectsSettingsRoute(onBackPressed = onBackPressed)
                    }

                    composable(route = libRedirectSettingsRoute) {
                        LibRedirectSettingsRoute(
                            onBackPressed = onBackPressed,
                            navController = navController,
                        )
                    }

                    composable(route = libRedirectServiceSettingsRoute) { _, _ ->
                        LibRedirectServiceSettingsRoute(onBackPressed = onBackPressed)
                    }

                    composable(route = downloaderSettingsRoute) {
                        DownloaderSettingsRoute(onBackPressed = onBackPressed)
                    }

                    composable(route = themeSettingsRoute) {
                        ThemeSettingsRoute(onBackPressed = onBackPressed)
                    }

                    composable(route = debugSettingsRoute) {
                        DebugSettingsRoute(
                            navController = navController,
                            onBackPressed = onBackPressed
                        )
                    }

                    composable(route = logViewerSettingsRoute) {
                        LogSettingsRoute(
                            navController = navController,
                            onBackPressed = onBackPressed
                        )
                    }

                    composable(route = logTextViewerSettingsRoute) { _, _ ->
                        LogTextSettingsRoute(onBackPressed = onBackPressed)
                    }

                    composable(route = aboutSettingsRoute) {
                        AboutSettingsRoute(
                            navController = navController,
                            onBackPressed = onBackPressed
                        )
                    }

                    composable(route = creditsSettingsRoute) {
                        CreditsSettingsRoute(onBackPressed = onBackPressed)
                    }

                    composable(route = preferredBrowserSettingsRoute) {
                        PreferredBrowserSettingsRoute(onBackPressed = onBackPressed)
                    }

                    composable(route = inAppBrowserSettingsRoute) {
                        InAppBrowserSettingsRoute(onBackPressed = onBackPressed)
                    }

                    composable(route = preferredAppsSettingsRoute) {
                        PreferredAppSettingsRoute(onBackPressed = onBackPressed)
                    }

                    if (AndroidVersion.AT_LEAST_API_31_S) {
                        composable(route = appsWhichCanOpenLinksSettingsRoute) {
                            AppsWhichCanOpenLinksSettingsRoute(onBackPressed = onBackPressed)
                        }
                    }
                }
            }
        }
    }
}
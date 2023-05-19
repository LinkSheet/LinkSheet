package fe.linksheet.activity

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fe.linksheet.aboutSettingsRoute
import fe.linksheet.appsSettingsRoute
import fe.linksheet.appsWhichCanOpenLinksSettingsRoute
import fe.linksheet.bottomSheetSettingsRoute
import fe.linksheet.browserSettingsRoute
import fe.linksheet.composable.main.MainRoute
import fe.linksheet.composable.settings.SettingsRoute
import fe.linksheet.composable.settings.SettingsViewModel
import fe.linksheet.composable.settings.about.AboutSettingsRoute
import fe.linksheet.composable.settings.about.CreditsSettingsRoute
import fe.linksheet.composable.settings.apps.AppsSettingsRoute
import fe.linksheet.composable.settings.apps.link.AppsWhichCanOpenLinksSettingsRoute
import fe.linksheet.composable.settings.apps.preferred.PreferredAppSettingsRoute
import fe.linksheet.composable.settings.bottomsheet.BottomSheetSettingsRoute
import fe.linksheet.composable.settings.browser.BrowserSettingsRoute
import fe.linksheet.composable.settings.browser.inapp.InAppBrowserSettingsRoute
import fe.linksheet.composable.settings.browser.mode.PreferredBrowserSettingsRoute
import fe.linksheet.composable.settings.link.LibRedirectServiceSettingsRoute
import fe.linksheet.composable.settings.link.LibRedirectSettingsRoute
import fe.linksheet.composable.settings.link.LinksSettingsRoute
import fe.linksheet.composable.settings.theme.ThemeSettingsRoute
import fe.linksheet.creditsSettingsRoute
import fe.linksheet.inAppBrowserSettingsRoute
import fe.linksheet.libRedirectServiceSettingsRoute
import fe.linksheet.libRedirectSettingsRoute
import fe.linksheet.linksSettingsRoute
import fe.linksheet.mainRoute
import fe.linksheet.preferredAppsSettingsRoute
import fe.linksheet.preferredBrowserSettingsRoute
import fe.linksheet.settingsRoute
import fe.linksheet.themeSettingsRoute
import fe.linksheet.ui.theme.AppTheme
import fe.linksheet.util.composable
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val settingsViewModel = koinViewModel<SettingsViewModel>()

            AppTheme(theme = settingsViewModel.theme.value) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Surface(color = MaterialTheme.colorScheme.surface) {
                        Column(modifier = Modifier.fillMaxSize()) {
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

                                composable(route = libRedirectSettingsRoute) {
                                    LibRedirectSettingsRoute(
                                        onBackPressed = onBackPressed,
                                        navController = navController,
                                    )
                                }

                                composable(route = libRedirectServiceSettingsRoute) { _, _ ->
                                    LibRedirectServiceSettingsRoute(
                                        onBackPressed = onBackPressed,
                                    )
                                }

                                composable(route = themeSettingsRoute) {
                                    ThemeSettingsRoute(
                                        onBackPressed = onBackPressed,
                                        viewModel = settingsViewModel
                                    )
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
                                    PreferredBrowserSettingsRoute(
                                        onBackPressed = onBackPressed,
                                        settingsViewModel = settingsViewModel
                                    )
                                }

                                composable(route = inAppBrowserSettingsRoute) {
                                    InAppBrowserSettingsRoute(
                                        onBackPressed = onBackPressed,
                                        settingsViewModel = settingsViewModel
                                    )
                                }

                                composable(route = preferredAppsSettingsRoute) {
                                    PreferredAppSettingsRoute(
                                        onBackPressed = onBackPressed,
                                        settingsViewModel = settingsViewModel
                                    )
                                }

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                    composable(route = appsWhichCanOpenLinksSettingsRoute) {
                                        AppsWhichCanOpenLinksSettingsRoute(
                                            onBackPressed = onBackPressed,
                                            settingsViewModel = settingsViewModel
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
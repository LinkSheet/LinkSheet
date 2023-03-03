package fe.linksheet.activity

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fe.linksheet.*
import fe.linksheet.composable.main.MainRoute
import fe.linksheet.composable.settings.SettingsRoute
import fe.linksheet.composable.settings.SettingsViewModel
import fe.linksheet.composable.settings.about.AboutSettingsRoute
import fe.linksheet.composable.settings.about.CreditsSettingsRoute
import fe.linksheet.composable.settings.apps.AppsSettingsRoute
import fe.linksheet.composable.settings.bottomsheet.BottomSheetSettingsRoute
import fe.linksheet.composable.settings.apps.browser.PreferredBrowserSettingsRoute
import fe.linksheet.composable.settings.apps.link.AppsWhichCanOpenLinksSettingsRoute
import fe.linksheet.composable.settings.apps.preferred.PreferredAppSettingsRoute
import fe.linksheet.composable.settings.links.LinksSettingsRoute
import fe.linksheet.composable.settings.theme.ThemeSettingsRoute
import fe.linksheet.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    private val settingsViewModel by viewModels<SettingsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            AppTheme(theme = settingsViewModel.theme) {
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

                                composable(route = bottomSheetSettingsRoute) {
                                    BottomSheetSettingsRoute(
                                        onBackPressed = onBackPressed,
                                        viewModel = settingsViewModel
                                    )
                                }

                                composable(route = linksSettingsRoute) {
                                    LinksSettingsRoute(
                                        onBackPressed = onBackPressed,
                                        viewModel = settingsViewModel
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
                                        viewModel = settingsViewModel
                                    )
                                }

                                composable(route = preferredAppsSettingsRoute) {
                                    PreferredAppSettingsRoute(
                                        onBackPressed = onBackPressed, viewModel = settingsViewModel
                                    )
                                }

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                    composable(route = appsWhichCanOpenLinksSettingsRoute) {
                                        AppsWhichCanOpenLinksSettingsRoute(
                                            onBackPressed = onBackPressed,
                                            viewModel = settingsViewModel
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
package fe.linksheet.activity

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.junkfood.seal.ui.common.animatedComposable
import fe.linksheet.*
import fe.linksheet.R
import fe.linksheet.composable.apps.AppsWhichCanOpenLinksSettingsRoute
import fe.linksheet.composable.browser.PreferredBrowserSettingsRoute
import fe.linksheet.composable.main.MainRoute
import fe.linksheet.composable.preferred.PreferredAppSettingsRoute
import fe.linksheet.composable.settings.SettingsRoute
import fe.linksheet.ui.theme.AppTheme
import fe.linksheet.ui.theme.HkGroteskFontFamily

class MainActivity : ComponentActivity() {
    @OptIn(
        ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class,
        ExperimentalAnimationApi::class
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            AppTheme {
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

                                composable(route = settingsRoute) {
                                    SettingsRoute(navController = navController)
                                }

                                composable(route = preferredBrowserSettingsRoute) {
                                    PreferredBrowserSettingsRoute(
                                        navController = navController,
                                        onBackPressed = { navController.popBackStack() })
                                }

                                composable(route = preferredAppsSettingsRoute) {
                                    PreferredAppSettingsRoute(
                                        navController = navController,
                                        onBackPressed = { navController.popBackStack() })
                                }

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                    composable(route = appsWhichCanOpenLinksSettingsRoute) {
                                        AppsWhichCanOpenLinksSettingsRoute(
                                            navController = navController,
                                            onBackPressed = {
                                                navController.popBackStack()
                                            }
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
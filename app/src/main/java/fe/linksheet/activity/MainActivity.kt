package fe.linksheet.activity

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fe.linksheet.*
import fe.linksheet.R
import fe.linksheet.composable.apps.AppsWhichCanOpenLinksSettingsRoute
import fe.linksheet.composable.browser.PreferredBrowserSettingsRoute
import fe.linksheet.composable.preferred.PreferredAppSettingsRoute
import fe.linksheet.composable.settings.SettingsRoute
import fe.linksheet.ui.theme.AppTheme
import fe.linksheet.ui.theme.HkGroteskFontFamily

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            AppTheme {
                Scaffold(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.background
                ) { padding ->
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .consumeWindowInsets(padding)
                            .windowInsetsPadding(
                                WindowInsets.safeDrawing.only(
                                    WindowInsetsSides.Horizontal,
                                ),
                            ),
                    ) {
                        Surface(color = MaterialTheme.colorScheme.surface) {
                            Column(modifier = Modifier.fillMaxSize()) {
                                TopAppBar(
                                    title = {
                                        Text(
                                            text = stringResource(id = R.string.app_name),
                                            fontWeight = FontWeight.SemiBold,
                                            fontFamily = HkGroteskFontFamily,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                )

                                Spacer(modifier = Modifier.height(5.dp))

                                NavHost(
                                    navController = navController,
                                    startDestination = settingsRoute,
                                ) {
                                    composable(route = settingsRoute) {
                                        SettingsRoute(navController = navController)
                                    }

                                    composable(route = preferredBrowserSettingsRoute){
                                        PreferredBrowserSettingsRoute(navController = navController)
                                    }

                                    composable(route = preferredAppsSettingsRoute) {
                                        PreferredAppSettingsRoute(navController = navController)
                                    }

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                        composable(route = appsWhichCanOpenLinksSettingsRoute) {
                                            AppsWhichCanOpenLinksSettingsRoute(navController = navController)
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
}
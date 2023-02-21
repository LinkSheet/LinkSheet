package fe.linksheet.activity

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
import fe.linksheet.R
import fe.linksheet.composable.preferred.PreferredSettingsRoute
import fe.linksheet.composable.settings.SettingsRoute
import fe.linksheet.preferredSettingsRoute
import fe.linksheet.settingsRoute
import fe.linksheet.ui.theme.HkGroteskFontFamily

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            Scaffold(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onBackground
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
                    Column(modifier = Modifier.fillMaxSize()) {
                        TopAppBar(
                            title = {
                                Text(
                                    text = stringResource(id = R.string.app_name),
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = HkGroteskFontFamily
                                )
                            }
                        )

                        Spacer(modifier = Modifier.height(5.dp))

                        NavHost(
                            navController = navController,
                            startDestination = settingsRoute,
                        ) {
                            composable(route = settingsRoute) {
                                SettingsRoute(navController)
                            }

                            composable(route = preferredSettingsRoute){
                                PreferredSettingsRoute(navController)
                            }
                        }
                    }
                }
            }
        }
    }
}
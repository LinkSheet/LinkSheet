package fe.linksheet.composable.settings.about

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.junkfood.seal.ui.component.BackButton
import fe.linksheet.BuildConfig
import fe.linksheet.R
import fe.linksheet.composable.SettingsItemRow
import fe.linksheet.composable.SwitchRow
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.settings.SettingsViewModel
import fe.linksheet.creditsSettingsRoute
import fe.linksheet.extension.openLink
import fe.linksheet.ui.theme.HkGroteskFontFamily


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutSettingsRoute(
    navController: NavHostController,
    onBackPressed: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    val context = LocalContext.current

    SettingsScaffold(R.string.about, onBackPressed = onBackPressed) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxHeight(),
            contentPadding = PaddingValues(horizontal = 5.dp)
        ) {
            item(creditsSettingsRoute) {
                SettingsItemRow(
                    navController = navController,
                    navigateTo = creditsSettingsRoute,
                    headline = R.string.credits,
                    subtitle = R.string.credits_explainer
                )
            }

            item("github") {
                SettingsItemRow(
                    headline = R.string.github,
                    subtitle = R.string.github_explainer,
                    onClick = {
                        context.openLink("https://github.com/1fexd/LinkSheet")
                    }
                )
            }

            item("donate"){
                SettingsItemRow(
                    headline = R.string.donate,
                    subtitle = R.string.donate_explainer,
                    onClick = {
                        context.openLink("https://coindrop.to/fexd")
                    }
                )
            }

            item("version") {
                SettingsItemRow(
                    headline = stringResource(id = R.string.version),
                    subtitle = BuildConfig.VERSION_NAME,
                    onClick = {}
                )
            }
        }
    }
}

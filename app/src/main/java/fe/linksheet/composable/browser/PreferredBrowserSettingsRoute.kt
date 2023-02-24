package fe.linksheet.composable.browser

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.tasomaniac.openwith.resolver.BrowserHandler
import fe.linksheet.R
import fe.linksheet.composable.ClickableRow
import fe.linksheet.composable.settings.SettingsViewModel
import fe.linksheet.extension.observeAsState
import fe.linksheet.ui.theme.HkGroteskFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferredBrowserSettingsRoute(
    navController: NavHostController,
    viewModel: SettingsViewModel = viewModel()
) {
    val context = LocalContext.current
    val lifecycleState = LocalLifecycleOwner.current.lifecycle.observeAsState()

    LaunchedEffect(Unit) {
        viewModel.loadBrowsers(context)
    }

    LaunchedEffect(lifecycleState.first) {
        if (lifecycleState.first == Lifecycle.Event.ON_RESUME && lifecycleState.second != Lifecycle.Event.ON_START) {
            viewModel.loadBrowsers(context)
        }
    }

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = stringResource(id = R.string.preferred_browser),
            fontFamily = HkGroteskFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(10.dp))
        LazyColumn {
            item(key = "none") {
                BrowserRow(
                    selected = viewModel.browserMode == BrowserHandler.BrowserMode.None,
                    onClick = {
                        viewModel.onBrowserMode(BrowserHandler.BrowserMode.None)
                    }
                ) {
                    Texts(headline = R.string.none, subtitle = R.string.none_explainer)
                }
            }

            item(key = "always_ask") {
                BrowserRow(
                    selected = viewModel.browserMode == BrowserHandler.BrowserMode.AlwaysAsk,
                    onClick = {
                        viewModel.onBrowserMode(BrowserHandler.BrowserMode.AlwaysAsk)
                    }
                ) {
                    Texts(headline = R.string.always_ask, subtitle = R.string.always_ask_explainer)
                }
            }

            viewModel.browsers.forEach { app ->
                item(key = app.packageName) {
                    BrowserRow(
                        selected = viewModel.browserMode == BrowserHandler.BrowserMode.SelectedBrowser && viewModel.selectedBrowser == app.flatComponentName,
                        onClick = {
                            viewModel.onBrowserMode(BrowserHandler.BrowserMode.SelectedBrowser)
                            viewModel.onSelectedBrowser(app.flatComponentName)
                        }
                    ) {
                        Image(
                            bitmap = app.getBitmap(context),
                            contentDescription = app.displayLabel,
                            modifier = Modifier.size(32.dp)
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = app.displayLabel, fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontFamily = HkGroteskFontFamily,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BrowserRow(
    selected: Boolean,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    ClickableRow(
        padding = 2.dp,
        onClick = onClick,
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        Spacer(modifier = Modifier.width(5.dp))
        content()
    }
}

@Composable
private fun Texts(@StringRes headline: Int, @StringRes subtitle: Int) {
    Column {
        Text(
            text = stringResource(id = headline),
            fontFamily = HkGroteskFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(text = stringResource(id = subtitle))
    }
}
package fe.linksheet.composable.settings.general

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fe.linksheet.R
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.util.SwitchRow
import fe.linksheet.extension.compose.currentActivity
import fe.linksheet.module.viewmodel.GeneralSettingsViewModel
import org.koin.androidx.compose.koinViewModel


@Composable
fun GeneralSettingsRoute(
    onBackPressed: () -> Unit,
    viewModel: GeneralSettingsViewModel = koinViewModel()
) {
    val context = LocalContext.currentActivity()

    SettingsScaffold(R.string.general, onBackPressed = onBackPressed) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxHeight(),
            contentPadding = PaddingValues(horizontal = 5.dp)
        ) {
            item(key = "always_show_package_name") {
                SwitchRow(
                    state = viewModel.alwaysShowPackageName,
                    viewModel = viewModel,
                    headline = stringResource(id = R.string.always_show_package_name),
                    subtitle = stringResource(id = R.string.always_show_package_name_explainer)
                )
            }
        }
    }
}
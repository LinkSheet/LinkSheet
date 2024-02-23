package fe.linksheet.composable.settings.privacy

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fe.linksheet.R
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.util.SwitchRow
import fe.linksheet.module.viewmodel.PrivacySettingsViewModel
import org.koin.androidx.compose.koinViewModel


@Composable
fun PrivacySettingsRoute(
    onBackPressed: () -> Unit,
    viewModel: PrivacySettingsViewModel = koinViewModel()
) {
    SettingsScaffold(R.string.privacy, onBackPressed = onBackPressed) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxHeight(),
            contentPadding = PaddingValues(horizontal = 5.dp)
        ) {
            item(key = "include_referrer") {
                SwitchRow(
                    state = viewModel.showAsReferrer,
                    headline = stringResource(id = R.string.show_linksheet_referrer),
                    subtitle = stringResource(id = R.string.show_linksheet_referrer_explainer)
                )
            }
        }
    }
}

package fe.linksheet.composable.settings.advanced

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import fe.linksheet.R
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.util.PreferenceSubtitle
import fe.linksheet.composable.util.SwitchRow
import fe.linksheet.module.viewmodel.FeatureFlagViewModel
import fe.linksheet.module.viewmodel.Flag
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FeatureFlagSettingsRoute(
    navController: NavHostController,
    onBackPressed: () -> Unit,
    viewModel: FeatureFlagViewModel = koinViewModel(),
) {
    SettingsScaffold(R.string.feature_flags, onBackPressed = onBackPressed) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxHeight(),
            contentPadding = PaddingValues(horizontal = 5.dp)
        ) {
            stickyHeader(key = "header") {
                Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                    PreferenceSubtitle(
                        text = stringResource(id = R.string.feature_flags_explainer),
                        paddingHorizontal = 10.dp
                    )
                }
            }

            viewModel.flags.forEach { flag ->
                item {
                    if (flag is Flag.Full) {
                        SwitchRow(
                            state = flag.pref,
                            headlineId = flag.headlineId,
                            subtitleId = flag.subtitleId
                        )
                    } else if (flag is Flag.Simple) {
                        SwitchRow(state = flag.pref, headline = flag.text)
                    }
                }
            }
        }
    }
}

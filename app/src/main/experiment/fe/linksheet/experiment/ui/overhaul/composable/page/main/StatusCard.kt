package fe.linksheet.experiment.ui.overhaul.composable.page.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fe.linksheet.R
import fe.linksheet.experiment.ui.overhaul.composable.component.card.ClickableAlertCard2
import fe.linksheet.experiment.ui.overhaul.composable.util.Resource.Companion.textContent
import fe.linksheet.module.viewmodel.MainViewModel
import fe.linksheet.ui.LocalActivity
import org.koin.androidx.compose.koinViewModel

@Composable
fun StatusCard(viewModel: MainViewModel = koinViewModel()) {
    val activity = LocalActivity.current

    ClickableAlertCard2(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        contentDescription = null,
        headline = textContent(R.string.settings_main_setup_success__title_linksheet_setup_success),
        subtitle = textContent(R.string.settings_main_setup_success__text_linksheet_setup_success_info),
        imageVector = Icons.Rounded.CheckCircleOutline,
    ) {
//        Column {
//            Text(text = stringResource(id = R.string.settings_main_setup_success__subtitle_quick_settings))

        LazyRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            item(key = R.string.settings_main_setup_success__button_change_browser) {
                Button(onClick = { viewModel.openDefaultBrowserSettings(activity) }) {
                    Text(text = stringResource(id = R.string.settings_main_setup_success__button_change_browser))
                }
            }

            item(key = R.string.settings_main_setup_success__button_link_handlers) {
                Button(onClick = { viewModel.openLinkHandlersSettings(activity) }) {
                    Text(text = stringResource(id = R.string.settings_main_setup_success__button_link_handlers))
                }
            }

            item(key = R.string.settings_main_setup_success__button_connected_apps) {
                Button(onClick = { viewModel.openCrossProfileAccess(activity) }) {
                    Text(text = stringResource(id = R.string.settings_main_setup_success__button_connected_apps))
                }
            }
        }
//        }
    }
}

@Preview
@Composable
fun StatusCardPreview() {
    StatusCard()
}

package fe.linksheet.experiment.ui.overhaul.composable.page.settings.app

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import fe.linksheet.R
import fe.linksheet.experiment.ui.overhaul.composable.component.page.SaneScaffoldSettingsPage
import fe.linksheet.experiment.ui.overhaul.interaction.LocalHapticFeedbackInteraction

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NewRuleRoute(
    onBackPressed: () -> Unit,
//    viewModel: AppConfigViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val interaction = LocalHapticFeedbackInteraction.current

    SaneScaffoldSettingsPage(
        headline = stringResource(id = R.string.settings_new_rule__title_new_rule),
        onBackPressed = onBackPressed,
        floatingActionButton = {
//            ExtendedFloatingActionButton(
//                modifier = Modifier.padding(paddingValues = WindowInsets.navigationBars.asPaddingValues()),
//                text = { Text(text = stringResource(id = R.string.settings_app_config_rules__btn_new_rule)) },
//                icon = { Icon(imageVector = Icons.Outlined.Add, contentDescription = null) },
//                onClick = {
//
//                }
//            )
        }
    ) {

    }
}


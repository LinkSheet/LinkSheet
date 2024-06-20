package fe.linksheet.experiment.ui.overhaul.composable.page.settings.app

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import fe.linksheet.R
import fe.linksheet.experiment.ui.overhaul.composable.ContentTypeDefaults
import fe.linksheet.experiment.ui.overhaul.composable.component.card.ClickableAlertCard2
import fe.linksheet.experiment.ui.overhaul.composable.component.page.SaneScaffoldSettingsPage
import fe.linksheet.experiment.ui.overhaul.composable.util.Resource.Companion.textContent
import fe.linksheet.experiment.ui.overhaul.interaction.LocalHapticFeedbackInteraction

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RuleOverviewRoute(
    onBackPressed: () -> Unit,
//    viewModel: AppConfigViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val interaction = LocalHapticFeedbackInteraction.current

    SaneScaffoldSettingsPage(
        headline = stringResource(id = R.string.settings_app_config_rules__title_rules),
        onBackPressed = onBackPressed,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                modifier = Modifier.padding(paddingValues = WindowInsets.navigationBars.asPaddingValues()),
                text = { Text(text = stringResource(id = R.string.settings_app_config_rules__btn_new_rule)) },
                icon = { Icon(imageVector = Icons.Outlined.Add, contentDescription = null) },
                onClick = {

                }
            )
        }
    ) {
        item(key = R.string.settings_app_config_rules__title_default_rule, contentType = ContentTypeDefaults.ClickableAlert) {
            ClickableAlertCard2(
                headline = textContent(R.string.settings_app_config_rules__title_default_rule),
                subtitle = textContent(R.string.settings_app_config_rules__subtitle_default_rule),
                imageVector = Icons.Outlined.Grade,
                contentDescription = null,
            )
        }

        divider(stringRes = R.string.settings_app_config_rules__text_custom_rules)
    }
}

@Composable
private fun RuleItem(){

}

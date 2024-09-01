package fe.linksheet.experiment.ui.overhaul.composable.page.settings.app

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Grade
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import fe.android.compose.feedback.LocalHapticFeedbackInteraction
import fe.android.compose.icon.iconPainter
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.component.ContentType
import fe.composekit.component.card.AlertCard
import fe.linksheet.R
import fe.linksheet.experiment.ui.overhaul.composable.component.page.SaneScaffoldSettingsPage

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
        item(key = R.string.settings_app_config_rules__title_default_rule, contentType = ContentType.ClickableAlert) {
            AlertCard(
                headline = textContent(R.string.settings_app_config_rules__title_default_rule),
                subtitle = textContent(R.string.settings_app_config_rules__subtitle_default_rule),
                icon = Icons.Outlined.Grade.iconPainter,
                iconContentDescription = null,
            )
        }

        divider(id = R.string.settings_app_config_rules__text_custom_rules)
    }
}

@Composable
private fun RuleItem() {

}

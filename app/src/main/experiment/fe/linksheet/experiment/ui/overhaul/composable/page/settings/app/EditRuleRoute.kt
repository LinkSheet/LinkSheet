package fe.linksheet.experiment.ui.overhaul.composable.page.settings.app

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import fe.linksheet.R
import fe.linksheet.component.ContentTypeDefaults
import fe.linksheet.component.list.item.default.DefaultTwoLineIconClickableShapeListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.page.SaneScaffoldSettingsPage
import fe.linksheet.component.util.Default.Companion.text

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EditRuleRoute(
    onBackPressed: () -> Unit,
//    viewModel: AppConfigViewModel = koinViewModel(),
) {
//    val context = LocalContext.current
//    val interaction = LocalHapticFeedbackInteraction.current

    SaneScaffoldSettingsPage(
        headline = stringResource(id = R.string.settings_edit_rule__title_edit_rule),
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
        divider(text = "Condition", key = "Condition")

        item(key = R.string.donate, contentType = ContentTypeDefaults.SingleGroupItem) {
//            ClickableAlertCard2(imageVector = , contentDescription = , headline = ) {
//
//            }

//            AlertCard(imageVector = , contentDescriptionId = , headlineId = , subtitleId = )

            DefaultTwoLineIconClickableShapeListItem(
                headlineContent = text("Default"),
                supportingContent = text("When no other rule matches"),
//                icon = vector(Icons.Outlined.AutoAwesome),
                onClick = {  }
            )
        }

        divider(text = "Action", key = 1234)

        item(key = "action", contentType = ContentTypeDefaults.SingleGroupItem) {
            DefaultTwoLineIconClickableShapeListItem(
                headlineContent = text("Show bottomsheet"),
                supportingContent = text(""),
//                icon = vector(Icons.Outlined.AutoAwesome),
                onClick = {  }
            )
        }

//        divider(stringRes = R.string.)
    }
}

@Preview
@Composable
fun EditRuleRoutePreview(){
    EditRuleRoute(onBackPressed = {})
}

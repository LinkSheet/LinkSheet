package fe.linksheet.experiment.ui.overhaul.composable.page.settings.app

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import fe.android.compose.text.DefaultContent.Companion.text
import fe.composekit.component.ContentType
import fe.composekit.component.list.item.default.DefaultTwoLineIconClickableShapeListItem
import fe.linksheet.R
import fe.linksheet.experiment.ui.overhaul.composable.component.page.SaneScaffoldSettingsPage

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

        item(key = R.string.donate, contentType = ContentType.SingleGroupItem) {
//            ClickableAlertCard2(imageVector = , contentDescription = , headline = ) {
//
//            }

//            AlertCard(imageVector = , contentDescriptionId = , headlineId = , subtitleId = )

            DefaultTwoLineIconClickableShapeListItem(
                headlineContent = text("Default"),
                supportingContent = text("When no other rule matches"),
//                icon = Icons.Outlined.AutoAwesome.iconPainter,
                onClick = { }
            )
        }

        divider(text = "Action", key = 1234)

        item(key = "action", contentType = ContentType.SingleGroupItem) {
            DefaultTwoLineIconClickableShapeListItem(
                headlineContent = text("Show bottomsheet"),
                supportingContent = text(""),
//                icon = Icons.Outlined.AutoAwesome.iconPainter,
                onClick = { }
            )
        }

//        divider(id =  R.string.)
    }
}

@Preview
@Composable
fun EditRuleRoutePreview() {
    EditRuleRoute(onBackPressed = {})
}

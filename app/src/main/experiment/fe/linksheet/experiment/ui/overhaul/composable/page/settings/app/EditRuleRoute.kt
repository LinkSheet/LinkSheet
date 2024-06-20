package fe.linksheet.experiment.ui.overhaul.composable.page.settings.app

import android.content.pm.getInstalledPackagesCompat
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fe.linksheet.BuildConfig
import fe.linksheet.R
import fe.linksheet.experiment.ui.overhaul.composable.ContentTypeDefaults
import fe.linksheet.experiment.ui.overhaul.composable.component.card.AlertCard
import fe.linksheet.experiment.ui.overhaul.composable.component.card.AlertCardContentLayout
import fe.linksheet.experiment.ui.overhaul.composable.component.card.AlertCardDefaults
import fe.linksheet.experiment.ui.overhaul.composable.component.card.ClickableAlertCard2
import fe.linksheet.experiment.ui.overhaul.composable.component.icon.AppIconImage
import fe.linksheet.experiment.ui.overhaul.composable.component.icon.FilledIcon
import fe.linksheet.experiment.ui.overhaul.composable.component.list.base.ShapeListItemDefaults
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.default.DefaultTwoLineIconClickableShapeListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.page.SaneLargeTopAppBar
import fe.linksheet.experiment.ui.overhaul.composable.component.page.SaneScaffoldSettingsPage
import fe.linksheet.experiment.ui.overhaul.composable.component.page.SaneSettingsScaffold
import fe.linksheet.experiment.ui.overhaul.composable.component.page.layout.SaneLazyColumnPageLayout
import fe.linksheet.experiment.ui.overhaul.composable.component.page.layout.SaneLazyListScope
import fe.linksheet.experiment.ui.overhaul.composable.util.AnnotatedStringResource.Companion.annotated
import fe.linksheet.experiment.ui.overhaul.composable.util.ComposableTextContent.Companion.content
import fe.linksheet.experiment.ui.overhaul.composable.util.Default.Companion.text
import fe.linksheet.experiment.ui.overhaul.composable.util.ImageVectorIconType.Companion.vector
import fe.linksheet.experiment.ui.overhaul.composable.util.Resource.Companion.textContent
import fe.linksheet.experiment.ui.overhaul.composable.util.TextContent
import fe.linksheet.experiment.ui.overhaul.interaction.FeedbackType
import fe.linksheet.experiment.ui.overhaul.interaction.LocalHapticFeedbackInteraction
import fe.linksheet.extension.android.isUserApp
import fe.linksheet.extension.android.toImageBitmap
import fe.linksheet.extension.compose.atElevation
import sh.calvin.reorderable.ReorderableCollectionItemScope

import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

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

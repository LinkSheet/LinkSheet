package fe.linksheet.experiment.ui.overhaul.composable.page.settings.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.ClearAll
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.Shapes
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fe.fastforwardkt.FastForwardRules
import fe.linksheet.R
import fe.linksheet.composable.util.SettingsItemRow
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.util.SubtitleText
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.ClickableShapeListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.ShapeListItemDefaults
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.default.DefaultTwoLineIconClickableShapeListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.page.ListItemData
import fe.linksheet.experiment.ui.overhaul.composable.component.page.SaneScaffoldSettingsPage
import fe.linksheet.experiment.ui.overhaul.composable.component.page.layout.group
import fe.linksheet.experiment.ui.overhaul.composable.util.DrawableIconType.Companion.drawable
import fe.linksheet.experiment.ui.overhaul.composable.util.ImageVectorIconType.Companion.vector
import fe.linksheet.experiment.ui.overhaul.composable.util.Resource
import fe.linksheet.experiment.ui.overhaul.composable.util.Resource.Companion.textContent
import fe.linksheet.experiment.ui.overhaul.interaction.LocalHapticFeedbackInteraction
import fe.linksheet.mastodonRedirectGithub
import fe.linksheet.openLinkWithGithub
import fe.linksheet.sealGithub


internal object NewCreditsSettingsRouteData {
    val apps = arrayOf(
        ListItemData(
            drawable(R.drawable.app_openlinkwith),
            textContent(R.string.open_link_with),
            textContent(R.string.open_link_with_subtitle_2),
            additional = openLinkWithGithub
        ),
        ListItemData(
            drawable(R.drawable.app_mastodonredirect),
            textContent(R.string.mastodon_redirect),
            textContent(R.string.mastodon_redirect_subtitle_2),
            additional = mastodonRedirectGithub
        ),
        ListItemData(
            drawable(R.drawable.app_seal),
            textContent(R.string.settings_credits__app__name_seal),
            textContent(R.string.settings_credits__app__name_seal_desc),
            additional = sealGithub
        )
    )
}

@Composable
fun NewCreditsSettingsRoute(onBackPressed: () -> Unit) {
    val interaction = LocalHapticFeedbackInteraction.current

    SaneScaffoldSettingsPage(headline = stringResource(id = R.string.credits), onBackPressed = onBackPressed) {
        group(array = NewCreditsSettingsRouteData.apps) { app, padding, shape ->
            ClickableShapeListItem(
                shape = shape,
                padding = padding,
                headlineContent = app.headlineContent,
                supportingContent = app.subtitleContent,
                leadingContent = {
                    Box(modifier = ShapeListItemDefaults.BaseContentModifier, contentAlignment = Alignment.Center) {
                        Image(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape),
                            painter = app.icon!!.rememberPainter(),
                            contentDescription = null
                        )
                    }
                },
                onClick = {
                    interaction.openUri(app.additional!!, HapticFeedbackType.LongPress)
                }
            )
        }
    }
}


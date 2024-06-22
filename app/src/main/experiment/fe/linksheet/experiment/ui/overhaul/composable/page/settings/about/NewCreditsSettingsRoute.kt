package fe.linksheet.experiment.ui.overhaul.composable.page.settings.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fe.linksheet.*
import fe.linksheet.R
import fe.linksheet.component.list.base.ClickableShapeListItem
import fe.linksheet.component.list.base.ShapeListItemDefaults
import fe.linksheet.component.page.ListItemData
import fe.linksheet.experiment.ui.overhaul.composable.component.page.SaneScaffoldSettingsPage
import fe.linksheet.component.page.layout.group
import fe.linksheet.component.util.DrawableIconType.Companion.drawable
import fe.linksheet.component.util.Resource.Companion.textContent
import fe.linksheet.experiment.ui.overhaul.interaction.LocalHapticFeedbackInteraction


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
            textContent(R.string.settings_credits__app_name_seal),
            textContent(R.string.settings_credits__app_credit_reason_seal),
            additional = sealGithub
        ),
        ListItemData(
            drawable(R.drawable.app_gmsflags),
            textContent(R.string.settings_credits__app_name_gmsflags),
            textContent(R.string.settings_credits__app_credit_reason_gmsflags),
            additional = gmsFlagsGithub
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


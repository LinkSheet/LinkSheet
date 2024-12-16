package fe.linksheet.composable.page.home.card.news

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fe.android.compose.icon.IconPainter
import fe.android.compose.icon.iconPainter
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.component.card.AlertCard
import fe.linksheet.R


@Composable
fun ExperimentUpdatedCard(
    @StringRes titleId: Int = R.string.settings_main_experiment_news__title_experiment_state_updated,
    icon: IconPainter = Icons.Outlined.AutoAwesome.iconPainter,
    @StringRes contentId: Int = R.string.settings_main_experiment_news__text_experiment_state_updated,
    @StringRes buttonTextId: Int = R.string.generic__button_learn_more,
    onClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertCard(
        icon = icon,
        iconContentDescription = stringResource(id = titleId),
        headline = textContent(titleId),
        subtitle = textContent(contentId)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)) {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.generic__button_dismiss))
            }
            Button(onClick = onClick) {
                Text(text = stringResource(id = buttonTextId))

                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))

                Icon(
                    modifier = Modifier.size(ButtonDefaults.IconSize),
                    imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                    contentDescription = null,
                )
            }
        }
    }
}

@Preview
@Composable
private fun ExperimentUpdatedCardPreview() {
    ExperimentUpdatedCard(
//        titleId = R.string.settings_main_experiment_news__title_experiment_state_updated,
//        icon = Icons.Outlined.AutoAwesome.iconPainter,
//        contentId = R.string.settings_main_experiment_news__text_experiment_state_updated,
//        buttonTextId = R.string.settings_main_news__button_read_more,
        onClick = {

        },
        onDismiss = {}
    )
}

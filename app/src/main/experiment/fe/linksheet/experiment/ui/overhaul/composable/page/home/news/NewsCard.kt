package fe.linksheet.experiment.ui.overhaul.composable.page.home.news

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import fe.linksheet.R
import fe.linksheet.experiment.ui.overhaul.composable.component.card.ClickableAlertCard2
import fe.linksheet.experiment.ui.overhaul.composable.util.Resource.Companion.textContent

@Composable
fun NewsCard(
    @StringRes titleId: Int,
    icon: ImageVector,
    @StringRes contentId: Int,
    @StringRes buttonTextId: Int,
    onClick: () -> Unit
) {
    ClickableAlertCard2(
        imageVector = icon,
        contentDescription = stringResource(id = titleId),
        headline = textContent(titleId),
        subtitle = textContent(contentId)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
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
fun NewsCardPreview() {
    NewsCard(
        titleId = R.string.settings_main_news__title_ui_overhaul,
        icon = Icons.Outlined.AutoAwesome,
        contentId = R.string.settings_main_news__text_ui_overhaul,
        buttonTextId = R.string.settings_main_news__button_read_more,
        onClick = {

        }
    )
}

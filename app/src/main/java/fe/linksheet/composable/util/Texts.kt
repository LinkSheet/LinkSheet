package fe.linksheet.composable.util

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import fe.linksheet.ui.HkGroteskFontFamily


@Composable
fun SettingSpacerText(@StringRes contentTitleId: Int) {
    SettingSpacerText(contentTitle = stringResource(id = contentTitleId))
}

@Composable
fun SettingSpacerText(contentTitle: String) {
    Text(
        text = contentTitle,
        fontFamily = HkGroteskFontFamily,
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun HeadlineText(headline: String) {
    Text(
        text = headline,
        fontFamily = HkGroteskFontFamily,
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
fun HeadlineText(@StringRes headline: Int) {
    HeadlineText(headline = stringResource(id = headline))
}

@Composable
fun SubtitleText(
    fontStyle: FontStyle? = null,
    subtitle: String
) {
    Text(
        text = subtitle,
        fontStyle = fontStyle,
        fontSize = 16.sp,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
fun SubtitleText(@StringRes subtitleId: Int) {
    SubtitleText(subtitle = stringResource(id = subtitleId))
}

@Composable
fun Texts(
    headline: String,
    subtitle: String? = null,
    content: @Composable (ColumnScope.() -> Unit)? = null,
) {
    Column(verticalArrangement = Arrangement.Center) {
        HeadlineText(headline = headline)

        if (subtitle != null) {
            SubtitleText(subtitle = subtitle)
        }

        content?.invoke(this)
    }
}

@Composable
fun Texts(@StringRes headlineId: Int, @StringRes subtitleId: Int? = null) {
    Texts(
        headline = stringResource(id = headlineId),
        subtitle = subtitleId?.let { stringResource(id = it) }
    )
}
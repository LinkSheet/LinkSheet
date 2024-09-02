package fe.linksheet.composable.util

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import fe.linksheet.ui.HkGroteskFontFamily
import fe.linksheet.ui.NewTypography



@Composable
fun SettingSpacerText(modifier: Modifier = Modifier, contentTitle: String) {
    Text(
        modifier = modifier,
        text = contentTitle,
        fontFamily = HkGroteskFontFamily,
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun HeadlineText(modifier: Modifier = Modifier, headline: CharSequence) {
    if (headline is AnnotatedString) {
        Text(
            modifier = modifier,
            text = headline,
            style = NewTypography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    } else if (headline is String) {
        Text(
            modifier = modifier,
            text = headline,
            style = NewTypography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun HeadlineText(modifier: Modifier = Modifier, @StringRes headlineId: Int) {
    HeadlineText(modifier, headline = stringResource(id = headlineId))
}



fun buildEnabledSubtitle(
    modifier: Modifier = Modifier,
    subtitle: String?
): @Composable ((Boolean) -> Unit)? = if (subtitle != null) {
    { SubtitleText(modifier = modifier, subtitle = subtitle) }
} else null

@Composable
fun LinkableSubtitleText(modifier: Modifier = Modifier, @StringRes id: Int, enabled: Boolean) {
    LinkableTextView(
        modifier = modifier,
        id = id,
        enabled = enabled,
        style = LocalTextStyle.current.copy(
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 16.sp
        )
    )
}

@Composable
fun SubtitleText(
    modifier: Modifier = Modifier,
    fontStyle: FontStyle? = null,
    subtitle: CharSequence
) {
    if (subtitle is AnnotatedString) {
        Text(
            modifier = modifier,
            text = subtitle,
            fontStyle = fontStyle,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    } else if (subtitle is String) {
        Text(
            modifier = modifier,
            text = subtitle,
            fontStyle = fontStyle,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun Texts(
    modifier: Modifier = Modifier,
    headline: CharSequence,
    subtitle: CharSequence? = null,
    content: @Composable (ColumnScope.() -> Unit)? = null,
) {
    Column(verticalArrangement = Arrangement.Center) {
        HeadlineText(headline = headline)

        if (subtitle != null) {
            SubtitleText(modifier = modifier, subtitle = subtitle)
        }

        content?.invoke(this)
    }
}

@Composable
fun Texts(
    modifier: Modifier = Modifier,
    @StringRes headlineId: Int,
    @StringRes subtitleId: Int? = null
) {
    Texts(
        modifier = modifier,
        headline = stringResource(id = headlineId),
        subtitle = subtitleId?.let { stringResource(id = it) }
    )
}

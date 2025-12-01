package fe.linksheet.composable.util

import androidx.annotation.StringRes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.sp
import app.linksheet.compose.theme.LegacyTypography


@Composable
fun HeadlineText(modifier: Modifier = Modifier, headline: CharSequence) {
    if (headline is AnnotatedString) {
        Text(
            modifier = modifier,
            text = headline,
            style = LegacyTypography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    } else if (headline is String) {
        Text(
            modifier = modifier,
            text = headline,
            style = LegacyTypography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun HeadlineText(modifier: Modifier = Modifier, @StringRes headlineId: Int) {
    HeadlineText(modifier, headline = stringResource(id = headlineId))
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


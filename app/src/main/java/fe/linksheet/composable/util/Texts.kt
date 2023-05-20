package fe.linksheet.composable.util

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import fe.linksheet.ui.HkGroteskFontFamily

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
fun SubtitleText(subtitle: String) {
    Text(
        text = subtitle,
        fontSize = 16.sp,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
fun SubtitleText(@StringRes subtitle: Int) {
    SubtitleText(subtitle = stringResource(id = subtitle))
}

@Composable
fun Texts(headline: String, subtitle: String? = null) {
    Column(verticalArrangement = Arrangement.Center) {
        HeadlineText(headline = headline)

        if (subtitle != null) {
            Text(text = subtitle, fontSize = 16.sp)
        }
    }
}

@Composable
fun Texts(@StringRes headline: Int, @StringRes subtitle: Int? = null) {
    Texts(
        headline = stringResource(id = headline),
        subtitle = subtitle?.let { stringResource(id = it) }
    )
}
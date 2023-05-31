package fe.linksheet.composable.util

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun SettingsItemRow(
    headline: String,
    subtitle: String,
    onClick: () -> Unit,
    image: @Composable (() -> Unit)? = null,
    content: (@Composable ColumnScope.() -> Unit)? = null
) {
    ClickableRow(
        padding = 10.dp,
        onClick = onClick,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (image != null) {
            image()
            Spacer(modifier = Modifier.width(15.dp))
        }

        Column {
            HeadlineText(headline = headline)
            SubtitleText(subtitle = subtitle)
            content?.invoke(this)
        }
    }
}

@Composable
fun SettingsItemRow(
    @StringRes headline: Int,
    @StringRes subtitle: Int,
    onClick: () -> Unit,
    image: @Composable (() -> Unit)? = null
) {
    SettingsItemRow(
        headline = stringResource(id = headline),
        subtitle = stringResource(id = subtitle),
        onClick = onClick,
        image = image
    )
}

@Composable
fun SettingsItemRow(
    navController: NavController,
    navigateTo: String,
    @StringRes headline: Int,
    @StringRes subtitle: Int,
    image: @Composable (() -> Unit)? = null
) {
    SettingsItemRow(headline = headline, subtitle = subtitle, image = image, onClick = {
        navController.navigate(navigateTo)
    })
}
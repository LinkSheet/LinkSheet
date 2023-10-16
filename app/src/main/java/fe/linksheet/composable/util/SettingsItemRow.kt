package fe.linksheet.composable.util

import androidx.annotation.StringRes
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
    headline: CharSequence,
    subtitle: CharSequence,
    onClick: (() -> Unit)? = null,
    image: @Composable (() -> Unit)? = null,
    content: (@Composable ColumnScope.() -> Unit)? = null
) {
    ClickableRow(
        onClick = onClick,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (image != null) {
            image()
            Spacer(modifier = Modifier.width(15.dp))
        }

        Texts(headline = headline, subtitle = subtitle, content = content)
    }
}

@Composable
fun SettingsItemRow(
    @StringRes headlineId: Int,
    @StringRes subtitleId: Int,
    onClick: (() -> Unit)? = null,
    image: @Composable (() -> Unit)? = null
) {
    SettingsItemRow(
        headline = stringResource(id = headlineId),
        subtitle = stringResource(id = subtitleId),
        onClick = onClick,
        image = image
    )
}

@Composable
fun SettingsItemRow(
    navController: NavController,
    navigateTo: String,
    @StringRes headlineId: Int,
    @StringRes subtitleId: Int,
    image: @Composable (() -> Unit)? = null
) {
    SettingsItemRow(
        headlineId = headlineId,
        subtitleId = subtitleId,
        image = image,
        onClick = {
            navController.navigate(navigateTo)
        }
    )
}
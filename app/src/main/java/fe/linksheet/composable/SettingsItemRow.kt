package fe.linksheet.composable

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import fe.linksheet.R
import fe.linksheet.preferredBrowserSettingsRoute
import fe.linksheet.ui.theme.HkGroteskFontFamily

@Composable
fun SettingsItemRow(
    headline: String,
    subtitle: String,
    onClick: () -> Unit
) {
    ClickableRow(
        padding = 10.dp,
        onClick = onClick
    ) {
        Column {
            Text(
                text = headline,
                fontFamily = HkGroteskFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun SettingsItemRow(
    @StringRes headline: Int,
    @StringRes subtitle: Int,
    onClick: () -> Unit
) {
    SettingsItemRow(
        headline = stringResource(id = headline),
        subtitle = stringResource(id = subtitle),
        onClick = onClick
    )
}

@Composable
fun SettingsItemRow(
    navController: NavController,
    navigateTo: String,
    @StringRes headline: Int,
    @StringRes subtitle: Int
) {
    SettingsItemRow(headline = headline, subtitle = subtitle) {
        navController.navigate(navigateTo)
    }
}
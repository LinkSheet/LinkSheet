package fe.linksheet.composable.util

import android.widget.Space
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import fe.linksheet.ui.theme.HkGroteskFontFamily

@Composable
fun SettingsItemRow(
    headline: String,
    subtitle: String,
    onClick: () -> Unit,
    image: @Composable (() -> Unit)? = null
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
            Text(
                text = headline,
                fontFamily = HkGroteskFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
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
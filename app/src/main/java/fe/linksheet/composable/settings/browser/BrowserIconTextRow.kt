package fe.linksheet.composable.settings.browser

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fe.linksheet.resolver.DisplayActivityInfo
import fe.linksheet.R
import fe.linksheet.ui.HkGroteskFontFamily

@Composable
fun BrowserIconTextRow(
    app: DisplayActivityInfo,
    selected: Boolean,
    showSelectedText: Boolean,
    alwaysShowPackageName: Boolean
) {
    Image(
        bitmap = app.iconBitmap,
        contentDescription = app.label,
        modifier = Modifier.size(32.dp)
    )

    Spacer(modifier = Modifier.width(10.dp))

    Column {
        Text(
            text = app.label,
            color = MaterialTheme.colorScheme.onSurface,
            fontFamily = HkGroteskFontFamily,
            fontWeight = FontWeight.SemiBold
        )

        if (selected && showSelectedText) {
            Text(
                text = stringResource(id = R.string.selected_browser_explainer),
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        if (alwaysShowPackageName) {
            Text(
                text = app.packageName,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.tertiary
            )
        }
    }
}
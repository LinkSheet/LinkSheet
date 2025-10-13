package fe.linksheet.composable.page.settings.browser

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fe.linksheet.R
import fe.linksheet.composable.component.appinfo.AppInfoIcon
import app.linksheet.compose.theme.HkGroteskFontFamily
import fe.linksheet.feature.app.ActivityAppInfo

@Composable
fun BrowserIconTextRow(
    app: ActivityAppInfo,
    selected: Boolean,
    showSelectedText: Boolean,
    alwaysShowPackageName: Boolean,
) {
    val context = LocalContext.current


    AppInfoIcon(appInfo = app)

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

package fe.linksheet.composable.util

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fe.linksheet.ui.theme.HkGroteskFontFamily

@Composable
fun SwitchRow(
    modifier: Modifier = Modifier,
    checked: Boolean,
    onChange: (Boolean) -> Unit,
    @StringRes headlineId: Int,
    @StringRes subtitleId: Int
) {
    SwitchRow(
        modifier = modifier,
        checked = checked,
        onChange = onChange,
        headline = stringResource(id = headlineId),
        subtitle = stringResource(id = subtitleId)
    )
}

@Composable
fun SwitchRow(
    modifier: Modifier = Modifier,
    checked: Boolean,
    onChange: (Boolean) -> Unit,
    headline: String,
    subtitle: String
) {
    ClickableRow(
        modifier = modifier,
        padding = 10.dp,
        verticalAlignment = Alignment.CenterVertically,
        onClick = {
            onChange(!checked)
        }) {
        Column(modifier = Modifier.fillMaxWidth(0.8f)) {
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

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Switch(checked = checked, onCheckedChange = onChange)
        }
    }
}
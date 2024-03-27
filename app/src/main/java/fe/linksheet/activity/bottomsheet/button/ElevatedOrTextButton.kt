package fe.linksheet.activity.bottomsheet.button

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fe.linksheet.ui.HkGroteskFontFamily

@Composable
fun ElevatedOrTextButton(onClick: () -> Unit, @StringRes buttonText: Int) {
    ElevatedButton(modifier = Modifier
        .fillMaxWidth()
        .padding(start = 15.dp, end = 15.dp),
        onClick = onClick,
        content = { Text(text = stringResource(id = buttonText)) })
}

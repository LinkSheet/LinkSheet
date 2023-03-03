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
import fe.linksheet.ui.theme.HkGroteskFontFamily

@Composable
fun Texts(@StringRes headline: Int, @StringRes subtitle: Int? = null) {
    Column(verticalArrangement = Arrangement.Center) {
        Text(
            text = stringResource(id = headline),
            fontFamily = HkGroteskFontFamily,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        if(subtitle != null) {
            Text(text = stringResource(id = subtitle), fontSize = 16.sp)
        }
    }
}
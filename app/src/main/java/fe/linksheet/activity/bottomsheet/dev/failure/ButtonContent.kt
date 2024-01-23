package fe.linksheet.activity.bottomsheet.dev.failure

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fe.linksheet.ui.HkGroteskFontFamily

@Composable
fun ButtonContent(imageVector: ImageVector, @StringRes text: Int) {
    Icon(imageVector = imageVector, contentDescription = null)
    Spacer(modifier = Modifier.width(5.dp))
    Text(
        text = stringResource(id = text),
        fontFamily = HkGroteskFontFamily,
        maxLines = 1,
        fontWeight = FontWeight.SemiBold
    )
}

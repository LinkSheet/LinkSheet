package fe.linksheet.composable.component.appbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import fe.linksheet.R
import fe.linksheet.composable.ui.HkGroteskFontFamily


@Composable
fun SaneAppBarTitle(modifier: Modifier = Modifier, headline: String) {
    Text(
        modifier = modifier,
//        modifier = modifier.offset(y = (-1).dp),
        text = headline,
        fontFamily = HkGroteskFontFamily,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
fun SaneAppBarBackButton(
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit
) {
    IconButton(modifier = modifier, onClick = onBackPressed) {
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
            contentDescription = stringResource(R.string.back),
        )
    }
}

package fe.linksheet.activity.bottomsheet.failure

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fe.linksheet.R
import fe.linksheet.activity.bottomsheet.BottomSheetActivityImpl.Companion.buttonPadding
import fe.linksheet.activity.bottomsheet.BottomSheetActivityImpl.Companion.buttonRowHeight
import fe.linksheet.resolver.BottomSheetResult
import fe.linksheet.ui.HkGroteskFontFamily

@Composable
fun FailureSheetColumn(
    result: BottomSheetResult?,
    onShareClick: () -> Unit,
    onCopyClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.no_handlers_found),
            fontFamily = HkGroteskFontFamily,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = stringResource(id = R.string.no_handlers_found_explainer),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.tertiary
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = buttonRowHeight)
            .padding(buttonPadding)
    ) {
        TextOrElevatedButton(
            imageVector = Icons.Default.ContentCopy,
            text = R.string.copy_url,
            onClick = onCopyClick
        )

        Spacer(modifier = Modifier.width(2.dp))

        TextOrElevatedButton(
            imageVector = Icons.Default.Share,
            text = R.string.send_to,
            onClick = onShareClick
        )
    }
}


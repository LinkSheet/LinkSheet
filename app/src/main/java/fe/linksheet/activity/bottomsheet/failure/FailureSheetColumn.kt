package fe.linksheet.activity.bottomsheet.failure

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
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
import fe.linksheet.activity.BottomSheetActivity.Companion.buttonPadding
import fe.linksheet.activity.BottomSheetActivity.Companion.buttonRowHeight
import fe.linksheet.resolver.BottomSheetResult
import fe.linksheet.ui.HkGroteskFontFamily

@Composable
fun FailureSheetColumn(
    result: BottomSheetResult?,
    useTextShareCopyButtons: Boolean,
    onShareClick: () -> Unit,
    onCopyClick: () -> Unit
) {
    val hasNoHandlers = result is BottomSheetResult.BottomSheetNoHandlersFound

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (hasNoHandlers) {
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
        } else {
            Text(
                text = stringResource(id = R.string.loading_link),
                fontFamily = HkGroteskFontFamily,
                fontWeight = FontWeight.SemiBold
            )
        }

        if (!hasNoHandlers) {
            Spacer(modifier = Modifier.height(10.dp))
            CircularProgressIndicator()
        }
    }

    if (hasNoHandlers) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = buttonRowHeight)
                .padding(buttonPadding)
        ) {
            TextOrElevatedButton(
                isTextBasedButton = useTextShareCopyButtons,
                imageVector = Icons.Default.ContentCopy,
                text = R.string.copy_url,
                onClick = onCopyClick
            )

            Spacer(modifier = Modifier.width(2.dp))

            TextOrElevatedButton(
                isTextBasedButton = useTextShareCopyButtons,
                imageVector = Icons.Default.Share,
                text = R.string.send_to,
                onClick = onShareClick
            )
        }
    }
}


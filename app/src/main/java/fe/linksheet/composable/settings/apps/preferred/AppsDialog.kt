package fe.linksheet.composable.settings.apps.preferred

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fe.android.compose.dialog.helper.OnClose
import fe.android.compose.dialog.helper.dialogHelper
import fe.linksheet.R
import fe.linksheet.composable.util.ClickableRow
import fe.linksheet.composable.util.DialogColumn
import fe.linksheet.composable.util.DialogContent
import fe.linksheet.composable.util.DialogSpacer
import fe.linksheet.composable.util.HeadlineText
import fe.linksheet.resolver.DisplayActivityInfo

data class AppsDialogCloseState(
    val displayActivityInfo: DisplayActivityInfo
)

@Composable
internal fun appsDialog(
    appsExceptPreferred: List<DisplayActivityInfo>?,
    alwaysShowPackageName: Boolean,
    onClose: OnClose<AppsDialogCloseState?> = {},
) = dialogHelper(
    state = appsExceptPreferred,
    onClose = onClose,
    notifyCloseNoState = false
) { state, close ->
    DialogColumn {
        HeadlineText(headlineId = R.string.select_an_app)
        DialogSpacer()
        DialogContent(
            items = state,
            key = { it.flatComponentName },
            bottomRow = {
                TextButton(onClick = { close(null) }) {
                    Text(text = stringResource(id = R.string.close))
                }
            },
            content = { info ->
                ClickableRow(
                    verticalAlignment = Alignment.CenterVertically,
                    padding = 5.dp,
                    onClick = { close(AppsDialogCloseState(info)) }
                ) {
                    Image(
                        bitmap = info.iconBitmap,
                        contentDescription = info.label,
                        modifier = Modifier.size(32.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        HeadlineText(headline = info.label)

                        if (alwaysShowPackageName) {
                            Text(
                                text = info.packageName,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                }
            }
        )
    }
}

package fe.linksheet.composable.settings.apps.preferred

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tasomaniac.openwith.resolver.DisplayActivityInfo
import fe.linksheet.R
import fe.linksheet.composable.util.ClickableRow
import fe.linksheet.composable.util.HeadlineText
import fe.linksheet.composable.util.OnClose
import fe.linksheet.composable.util.dialogHelper

data class AppsDialogCloseState(
    val displayActivityInfo: DisplayActivityInfo
)

@Composable
internal fun appsDialog(
    appsExceptPreferred: State<List<DisplayActivityInfo>>,
    alwaysShowPackageName: Boolean,
    onClose: OnClose<AppsDialogCloseState?> = {},
) = dialogHelper(
    state = appsExceptPreferred,
    onClose = onClose,
    notifyCloseNoState = false
) { state, close ->
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.surface)
        ) {
            HeadlineText(headline = R.string.select_an_app)
        }

        Spacer(modifier = Modifier.height(10.dp))
        Box {
            LazyColumn(modifier = Modifier.padding(bottom = 50.dp), content = {
                items(items = state.value, key = { it.flatComponentName }) { info ->
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
            })

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .height(40.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { close(null) }) {
                    Text(text = stringResource(id = R.string.close))
                }
            }
        }
    }
}

package fe.linksheet.composable.settings.link.redirect

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fe.android.compose.dialog.helper.OnClose
import fe.linksheet.R
import fe.linksheet.composable.util.DialogColumn
import fe.linksheet.composable.util.DialogContent
import fe.linksheet.composable.util.DialogSpacer
import fe.linksheet.composable.util.HeadlineText

@Composable
fun FollowRedirectsKnownTrackersDialog(
    trackers: List<String>,
    close: OnClose<Unit>,
) {
    DialogColumn {
        HeadlineText(headlineId = R.string.known_trackers)
        DialogSpacer()

        Spacer(modifier = Modifier.height(5.dp))

        Spacer(modifier = Modifier.height(5.dp))

        DialogContent(
            items = trackers,
            key = { it },
            bottomRow = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = {
                        close(Unit)
                    }) {
                        Text(text = stringResource(id = R.string.close))
                    }
                }
            },
            content = { tracker ->
                Text(text = tracker)
            }
        )
    }
}
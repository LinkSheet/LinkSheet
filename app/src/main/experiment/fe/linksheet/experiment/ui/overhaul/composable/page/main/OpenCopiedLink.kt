package fe.linksheet.experiment.ui.overhaul.composable.page.main

import android.content.Intent
import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import fe.linksheet.R
import fe.linksheet.activity.BottomSheetActivity
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.type.ClickableAlertListItem


@Composable
fun OpenCopiedLink(uri: Uri) {
    val context = LocalContext.current

    ClickableAlertListItem(
        onClick = {
            context.startActivity(Intent(context, BottomSheetActivity::class.java).apply {
                this.action = Intent.ACTION_VIEW
                this.data = uri
            })
        },
        imageVector = Icons.Default.ContentPaste,
        contentDescriptionText = stringResource(id = R.string.paste),
        headlineContentText = stringResource(id = R.string.open_copied_link),
        supportingContentText = uri.toString()
    )
}

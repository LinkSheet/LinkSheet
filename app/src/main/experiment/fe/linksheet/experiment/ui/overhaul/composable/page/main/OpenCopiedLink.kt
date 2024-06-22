package fe.linksheet.experiment.ui.overhaul.composable.page.main

import android.content.Intent
import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentPasteGo
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import fe.linksheet.R
import fe.linksheet.activity.BottomSheetActivity
import fe.linksheet.component.card.ClickableAlertCard2
import fe.linksheet.component.util.Default.Companion.text
import fe.linksheet.component.util.Resource.Companion.textContent


@Composable
fun OpenCopiedLink(uri: Uri) {
    val context = LocalContext.current

    ClickableAlertCard2(
        onClick = {
            context.startActivity(Intent(context, BottomSheetActivity::class.java).apply {
                this.action = Intent.ACTION_VIEW
                this.data = uri
            })
        },
        imageVector = Icons.Outlined.ContentPasteGo,
        contentDescription = stringResource(id = R.string.paste),
        headline = textContent(R.string.open_copied_link),
        subtitle = text(uri.toString())
    )
}

@Preview
@Composable
fun OpenCopiedLinkPreview() {
    OpenCopiedLink(uri = Uri.parse("https://google.com"))
}

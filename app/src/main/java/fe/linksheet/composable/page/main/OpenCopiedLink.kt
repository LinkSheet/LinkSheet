package fe.linksheet.composable.page.main

import android.content.Intent
import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentPasteGo
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import fe.android.compose.icon.iconPainter
import fe.android.compose.text.DefaultContent.Companion.text
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.component.card.AlertCard
import fe.linksheet.R
import fe.linksheet.activity.BottomSheetActivity


@Composable
fun OpenCopiedLink(uri: Uri) {
    val context = LocalContext.current

    AlertCard(
        onClick = {
            context.startActivity(Intent(context, BottomSheetActivity::class.java).apply {
                this.action = Intent.ACTION_VIEW
                this.data = uri
            })
        },
        icon = Icons.Outlined.ContentPasteGo.iconPainter,
        iconContentDescription = stringResource(id = R.string.paste),
        headline = textContent(R.string.open_copied_link),
        subtitle = text(uri.toString())
    )
}

@Preview
@Composable
fun OpenCopiedLinkPreview() {
    OpenCopiedLink(uri = Uri.parse("https://google.com"))
}

package ui_overhaul.fe.linksheet.composable.main

import android.content.Intent
import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import fe.linksheet.R
import fe.linksheet.activity.BottomSheetActivity


@Composable
fun OpenCopiedLink(uri: Uri) {
    val context = LocalContext.current
    MainCard(onClick = {
        context.startActivity(Intent(context, BottomSheetActivity::class.java).apply {
            this.action = Intent.ACTION_VIEW
            this.data = uri
        })
    }) {
        MainCardContent(icon = Icons.Default.ContentPaste, iconDescription = R.string.paste, title = R.string.open_copied_link, content =  uri.toString())
    }
}

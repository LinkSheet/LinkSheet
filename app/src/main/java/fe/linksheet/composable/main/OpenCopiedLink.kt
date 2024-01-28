package fe.linksheet.composable.main

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fe.linksheet.R
import fe.linksheet.composable.util.ColoredIcon
import fe.linksheet.ui.Typography


@Composable
fun OpenCopiedLink(uri: Uri) {
    val context = LocalContext.current
    OutlinedCard(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.clickable {
                context.startActivity(Intent(Intent.ACTION_VIEW, uri))
            },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(10.dp))

            ColoredIcon(icon = Icons.Default.ContentPaste, descriptionId = R.string.paste)

            Column(modifier = Modifier.padding(15.dp)) {
                Text(
                    text = stringResource(id = R.string.open_copied_link),
                    style = Typography.titleLarge,
                )
                Text(text = uri.toString())
            }
        }
    }
}

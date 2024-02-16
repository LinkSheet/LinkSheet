package fe.linksheet.activity.bottomsheet.dev

import android.content.ClipboardManager
import android.net.Uri
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.FastForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.getSystemService
import fe.linksheet.R
import fe.linksheet.extension.compose.runIf

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun UrlBar(
    uri: Uri,
    downloadable: Boolean,
    libRedirected: Boolean,
    copyUri: () -> Unit,
    shareUri: () -> Unit,
    downloadUri: (() -> Unit)? = null,
    ignoreLibRedirect: (() -> Unit)? = null
) {
    var showFullUrl by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 15.dp, end = 15.dp)
            .clip(CardDefaults.shape)
            .combinedClickable(onClick = {}, onLongClick = {
                showFullUrl = !showFullUrl
            })
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
                .runIf(!showFullUrl) { it.height(60.dp) }
                .padding(start = 10.dp, end = 5.dp, top = 10.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                modifier = Modifier.weight(1f),
                text = uri.toString(),
                maxLines = if (showFullUrl) Int.MAX_VALUE else 3,
                overflow = TextOverflow.Ellipsis,
                fontSize = 12.sp,
                lineHeight = 12.sp
            )

            Spacer(modifier = Modifier.width(10.dp))

            CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                if (downloadable) {
                    IconButton(onClick = downloadUri!!) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = stringResource(id = R.string.download)
                        )
                    }
                }

                if (libRedirected) {
                    IconButton(onClick = ignoreLibRedirect!!) {
                        Icon(
                            imageVector = Icons.Outlined.FastForward,
                            contentDescription = stringResource(id = R.string.request_private_browsing)
                        )
                    }
                }

                IconButton(onClick = copyUri) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = stringResource(id = R.string.copy_url)
                    )
                }

                IconButton(onClick = shareUri) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = stringResource(id = R.string.copy_url)
                    )
                }
            }
        }
    }

    HorizontalDivider(
        modifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 10.dp, bottom = 10.dp),
        color = MaterialTheme.colorScheme.outline.copy(0.25f)
    )

}

@Preview(name = "UrlPreview", showBackground = true)
@Composable
private fun UrlBarPreview() {
    val clipboardManager = LocalContext.current.getSystemService<ClipboardManager>()!!

//    UrlBar(
//        uri = Uri.parse("https://developer.android.com/jetpack/compose/text/configure-layout"),
//        clipboardManager = clipboardManager,
//        urlCopiedToast = MockRepositoryState.preference(true),
//        hideAfterCopying = MockRepositoryState.preference(true),
//        showToast = {},
//        hideDrawer = {},
//        shareUri = {}
//    )
}

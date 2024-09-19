package fe.linksheet.activity.bottomsheet

import android.content.ClipboardManager
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.getSystemService
import fe.linksheet.R
import fe.linksheet.experiment.improved.resolver.activity.bottomsheet.CrossProfile
import me.saket.unfurl.UnfurlResult

@Composable
fun UrlBar(
    uri: String,
    unfurlResult: UnfurlResult?,
    profiles: List<CrossProfile>?,
    downloadable: Boolean,
    libRedirected: Boolean,
    copyUri: () -> Unit,
    shareUri: () -> Unit,
    switchProfile: ((CrossProfile) -> Unit)?,
    downloadUri: (() -> Unit)? = null,
    ignoreLibRedirect: (() -> Unit)? = null,
    onDoubleClick: (() -> Unit)? = null,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 15.dp, end = 15.dp)
    ) {
        UrlCard(uri = uri, unfurlResult = unfurlResult, onDoubleClick = onDoubleClick)

        Spacer(modifier = Modifier.height(5.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            item {
                UrlActionButton(text = R.string.copy_url, icon = Icons.Filled.ContentCopy, onClick = copyUri)
            }

            item {
                UrlActionButton(text = R.string.share, icon = Icons.Filled.Share, onClick = shareUri)
            }

            if(switchProfile != null && profiles != null){
                for (target in profiles) {
                    item {
                        UrlActionButton(
                            text = target.label,
                            icon = target.bitmap,
                            onClick = { switchProfile(target) }
                        )
                    }
                }
            }

            if (downloadable) {
                item {
                    UrlActionButton(text = R.string.download, icon = Icons.Filled.Download, onClick = downloadUri!!)
                }
            }

            if (libRedirected) {
                item {
                    UrlActionButton(
                        text = R.string.ignore_libredirect,
                        icon = Icons.Filled.FastForward,
                        onClick = ignoreLibRedirect!!
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(5.dp))

        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(0.25f))

        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
private fun UrlActionButton(text: String, icon: ImageBitmap, onClick: () -> Unit) {
    ElevatedAssistChip(
        onClick = onClick,
        elevation = AssistChipDefaults.assistChipElevation(),
        shape = CircleShape,
        leadingIcon = {
            Icon(
                modifier = Modifier.size(18.dp),
                bitmap = icon,
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = text
            )
        },
        label = { Text(text = text, fontSize = 13.sp) }
    )
}

@Composable
private fun UrlActionButton(@StringRes text: Int, icon: ImageVector, onClick: () -> Unit) {
    ElevatedAssistChip(
        onClick = onClick,
        elevation = AssistChipDefaults.assistChipElevation(),
        shape = CircleShape,
        leadingIcon = {
            Icon(
                modifier = Modifier.size(18.dp),
                imageVector = icon,
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = stringResource(id = text)
            )
        },
        label = { Text(text = stringResource(id = text), fontSize = 13.sp) }
    )
}

@Preview(name = "UrlPreview", showBackground = true)
@Composable
private fun UrlBarPreview() {
    val clipboardManager = LocalContext.current.getSystemService<ClipboardManager>()!!


//    UrlBar(
//        uri = uri,
//        unfurlResult = unfurled,
//        downloadable = false,
//        libRedirected = false,
//        copyUri = { /*TODO*/ },
//        shareUri = { /*TODO*/ }
//    )

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

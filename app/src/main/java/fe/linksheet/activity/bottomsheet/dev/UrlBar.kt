package fe.linksheet.activity.bottomsheet.dev

import android.content.ClipboardManager
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.getSystemService
import fe.android.preference.helper.compose.StatePreference
import fe.android.preference.helper.compose.mock.MockRepositoryState
import fe.linksheet.R
import fe.linksheet.extension.android.setText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UrlBar(
    uri: Uri,
    clipboardManager: ClipboardManager,
    urlCopiedToast: StatePreference<Boolean>,
    hideAfterCopying: StatePreference<Boolean>,
    showToast: (Int) -> Unit,
    hideDrawer: () -> Unit,
    shareUri: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 15.dp, end = 15.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(start = 10.dp, end = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = uri.toString(),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                fontSize = 12.sp,
                lineHeight = 12.sp
            )

            Spacer(modifier = Modifier.width(10.dp))

            CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                IconButton(
                    onClick = {
                        clipboardManager.setText("URL", uri.toString())

                        if (urlCopiedToast()) {
                            showToast(R.string.url_copied)
                        }

                        if (hideAfterCopying()) {
                            hideDrawer()
                        }
                    }
                ) {
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

    UrlBar(
        uri = Uri.parse("https://developer.android.com/jetpack/compose/text/configure-layout"),
        clipboardManager = clipboardManager,
        urlCopiedToast = MockRepositoryState.preference(true),
        hideAfterCopying = MockRepositoryState.preference(true),
        showToast = {},
        hideDrawer = {},
        shareUri = {}
    )
}

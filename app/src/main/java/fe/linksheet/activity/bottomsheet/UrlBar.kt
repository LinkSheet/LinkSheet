package fe.linksheet.activity.bottomsheet

import android.content.ClipboardManager
import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.FastForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.getSystemService
import coil.compose.AsyncImage
import fe.linksheet.R
import fe.linksheet.extension.compose.runIf
import fe.linksheet.ui.HkGroteskFontFamily
import me.saket.unfurl.UnfurlResult
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun UrlBar(
    uri: String,
    unfurlResult: UnfurlResult?,
    downloadable: Boolean,
    libRedirected: Boolean,
    copyUri: () -> Unit,
    shareUri: () -> Unit,
    downloadUri: (() -> Unit)? = null,
    ignoreLibRedirect: (() -> Unit)? = null,
) {
    var showFullUrl by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth().padding(start = 15.dp, end = 15.dp)) {
        unfurlResult?.let { preview ->
            val thumbnailUrl = preview.thumbnail.toString()
            val faviconUrl = preview.favicon.toString()

            AsyncImage(
                model = thumbnailUrl,
                contentDescription = "",
                contentScale = ContentScale.FillWidth,
//                    placeholder = painterResource(R.drawable.placeholder),
//                    contentScale = ContentScale.Crop,
                modifier = Modifier
                    .heightIn(max = 200.dp)
                    .clip(CardDefaults.shape)
            )

            Spacer(modifier = Modifier.height(5.dp))

            if (preview.title != null) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = faviconUrl,
                        contentDescription = "",
                        contentScale = ContentScale.FillHeight,
                        modifier = Modifier.size(16.dp),
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = preview.title!!,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontFamily = HkGroteskFontFamily,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
            modifier = Modifier.fillMaxWidth().clip(CardDefaults.shape).combinedClickable(onClick = {}, onLongClick = {
                showFullUrl = !showFullUrl
            }),
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
                    text = uri,
                    maxLines = if (showFullUrl) Int.MAX_VALUE else 3,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 14.sp,
                    lineHeight = 14.sp
                )

                Spacer(modifier = Modifier.width(5.dp))

                CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
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
                }
            }
        }

        Spacer(modifier = Modifier.height(5.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            item {
                UrlActionButton(text = R.string.share, icon = Icons.Filled.Share, onClick = shareUri)
            }

            item {
                if (downloadable) {
                    UrlActionButton(text = R.string.download, icon = Icons.Filled.Download, onClick = downloadUri!!)
                }
            }
        }

        Spacer(modifier = Modifier.height(5.dp))

        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(0.25f))

        Spacer(modifier = Modifier.height(10.dp))
    }
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
    val uri = "https://www.youtube.com/watch?v=evIpx9Onc2c"

    val unfurled = UnfurlResult(
        url = uri.toHttpUrlOrNull()!!,
        title = "Grim Salvo x Savage Ga\$p - why do i still care?",
        description = "\"why do i still care?\"Prod. ³³marrowEdit by Zetsuboū絶望 (re:zero)Follow Savage Ga\$phttps://open.spotify.com/artist/0x7qiZJaal6j8qS7yCydFk?si=LAmKfXDwSc-V0BZoD...",
        favicon = "https://www.youtube.com/s/desktop/4feff1e2/img/favicon.ico".toHttpUrlOrNull(),
        thumbnail = "https://i.ytimg.com/vi/evIpx9Onc2c/maxresdefault.jpg?sqp=-oaymwEmCIAKENAF8quKqQMa8AEB-AH-CYAC0AWKAgwIABABGH8gOCgyMA8=&rs=AOn4CLB2ThnXsKlWHuEznGduSc7di30S-w".toHttpUrlOrNull()
    )
    UrlBar(
        uri = uri,
        unfurlResult = unfurled,
        downloadable = false,
        libRedirected = false,
        copyUri = { /*TODO*/ },
        shareUri = { /*TODO*/ }
    )

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

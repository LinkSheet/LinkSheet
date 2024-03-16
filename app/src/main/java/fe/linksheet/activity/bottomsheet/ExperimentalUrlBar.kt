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
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import coil.size.Dimension
import coil.size.Scale
import fe.linksheet.R
import fe.linksheet.extension.compose.runIf
import fe.linksheet.ui.HkGroteskFontFamily
import me.saket.unfurl.UnfurlResult
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ExperimentalUrlBar(
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
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 15.dp, end = 15.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
            modifier = Modifier
                .fillMaxWidth()
                .clip(CardDefaults.shape)
                .combinedClickable(onClick = {}, onLongClick = {
                    showFullUrl = !showFullUrl
                }),
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                val thumbnailUrl = unfurlResult?.thumbnail?.toString()
                val faviconUrl = unfurlResult?.favicon?.toString()

                if (thumbnailUrl != null) {
//                    var thumbnail by remember { mutableStateOf(true) }

//                    if (thumbnail) {
//                        AsyncImage(
//                            model = ImageRequest.Builder(context).data(thumbnailUrl)
//                                .size(height = Dimension(200), width = Dimension.Undefined)
//                                .scale(Scale.FIT).build(),
//                            onError = {
//                                thumbnail = false
//                            },
//                            contentDescription = "",
//                            modifier = Modifier.clip(CardDefaults.shape).border(1.dp, Color.Red),
//                            contentScale = ContentScale.FillWidth
//                        )
//                    }


                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(context).data(thumbnailUrl)
//                            .size(height = Dimension(200.dp.value.toInt()), width = Dimension.Undefined)
//                            .scale(Scale.FIT)
                            .build(),
                         contentScale = ContentScale.FillWidth,
                        contentDescription = ""
                    ) {
                        val state = painter.state
                        if (state is AsyncImagePainter.State.Success) {
                            SubcomposeAsyncImageContent(modifier = Modifier.heightIn(max = 200.dp)
                                .clip(CardDefaults.shape))
                        }
                    }
                }

                Row(
                    modifier = Modifier.padding(start = 5.dp, end = 5.dp, top = 5.dp, bottom = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (faviconUrl != null) {
//                        AsyncImage(
//                            model = ImageRequest.Builder(context).data(faviconUrl).size(16).build(),
//                            contentDescription = "",
//                            modifier = Modifier.border(1.dp, Color.Green),
//                        )

                        SubcomposeAsyncImage(model = faviconUrl, contentDescription = "") {
                            val state = painter.state
                            if (state is AsyncImagePainter.State.Success) {
                                SubcomposeAsyncImageContent(
                                    modifier = Modifier.size(16.dp),
                                    contentScale = ContentScale.FillHeight
                                )
                            }
                        }
                    }

                    Column(verticalArrangement = Arrangement.Center) {
                        if (unfurlResult?.title != null) {
                            Text(
                                text = unfurlResult.title!!,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontFamily = HkGroteskFontFamily,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Text(
                            text = uri,
                            maxLines = if (showFullUrl) Int.MAX_VALUE else 3,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 14.sp,
                            lineHeight = 14.sp
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

package fe.linksheet.activity.bottomsheet.content.success.url

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.compose.AsyncImagePainter
import coil3.compose.LocalPlatformContext
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import fe.android.compose.extension.thenIf
import fe.linksheet.composable.ui.HkGroteskFontFamily
import io.github.fornewid.placeholder.foundation.PlaceholderHighlight
import io.github.fornewid.placeholder.material3.placeholder
import io.github.fornewid.placeholder.material3.shimmer
import me.saket.unfurl.UnfurlResult
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@Composable
fun UrlCard(
    uri: String,
    unfurlResult: UnfurlResult?,
    imageLoader: ImageLoader?,
    onDoubleClick: (() -> Unit)? = null,
) {
    val data = when (unfurlResult) {
        null -> null
        else -> UrlCardData(unfurlResult.title, unfurlResult.favicon, unfurlResult.thumbnail)
    }

    UrlCard(
        uri = uri,
        data = data,
        imageLoader = imageLoader,
        onDoubleClick = onDoubleClick
    )
}

data class UrlCardData(
    val title: String?,
    val favicon: Any?,
    val thumbnail: Any?,
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UrlCard(
    uri: String,
    data: UrlCardData? = null,
    imageLoader: ImageLoader? = null,
    onDoubleClick: (() -> Unit)? = null,
) {
    val context = LocalContext.current
    var showFullUrl by remember { mutableStateOf(false) }
//    var expanded by remember { mutableStateOf(true) }

    val platform = LocalPlatformContext.current
    val imageLoader = remember {
        imageLoader ?: SingletonImageLoader.get(platform)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardDefaults.shape)
            .combinedClickable(
                onClick = {},
                onDoubleClick = onDoubleClick,
                onLongClick = {
                    showFullUrl = !showFullUrl
                }
            )
            .layoutId("url_card"),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            if (data?.thumbnail != null) {
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

//                    AsyncImage(model = , contentDescription = , imageLoader = )
//
                SubcomposeAsyncImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                        .testTag("thumbnail"),
                    model = data.thumbnail.toString(),
                    imageLoader = imageLoader,
                    alignment = Alignment.Center,
                    contentScale = ContentScale.FillWidth,
                    contentDescription = ""
                ) {
                    val state by painter.state.collectAsState()
                    if (state is AsyncImagePainter.State.Success) {
                        SubcomposeAsyncImageContent(modifier = Modifier.clip(CardDefaults.shape))
                    } else if (state is AsyncImagePainter.State.Empty || state is AsyncImagePainter.State.Loading) {
                        Spacer(
                            modifier = Modifier
                                .requiredHeight(200.dp)
                                .clip(CardDefaults.shape)
                                .placeholder(
                                    visible = true,
                                    highlight = PlaceholderHighlight.shimmer(),
                                )
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .thenIf(!showFullUrl && data == null) { it.height(60.dp) }
                    .padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (data?.favicon != null) {
//                        AsyncImage(
//                            model = ImageRequest.Builder(context).data(faviconUrl).size(16).build(),
//                            contentDescription = "",
//                            modifier = Modifier.border(1.dp, Color.Green),
//                        )

                    SubcomposeAsyncImage(
                        modifier = Modifier
                            .size(16.dp)
                            .testTag("favicon"),
                        // favicon has type Any -> unfurler passed HttpUrl -> call toString to ensure coil can handle it
                        model = data.favicon.toString(),
                        imageLoader = imageLoader,
                        contentDescription = ""
                    ) {
                        val state by painter.state.collectAsState()
                        LaunchedEffect(key1 = state) {
                            Log.d("UrlCard", "$state")
                        }

                        if (state is AsyncImagePainter.State.Success) {
                            SubcomposeAsyncImageContent(
                                modifier = Modifier.size(16.dp),
                                contentScale = ContentScale.FillHeight
                            )
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.Center) {
                    if (data?.title != null) {
                        Text(
                            text = data.title,
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
                    )
                }
            }
        }
    }
}

private class UrlCardPreviewProvider : PreviewParameterProvider<UrlCardData> {
    @OptIn(ExperimentalEncodingApi::class)
    override val values: Sequence<UrlCardData> = sequenceOf(
        UrlCardData(
            title = "",
            favicon = Base64.decode(
                """AAABAAEAEBAAAAEAIABoBAAAFgAAACgAAAAQAAAAIAAAAAEAIAAAAAAAAAQAABILAAASCwAAAAAAAAAAAAD///8A////AP///wD///8A////AP///wD///8A////AP///wD///8A////AP///wD///8A////AP///wD///8A////AP///wD///8A////AP///wD///8A////AP///wD///8A////AP///wD///8A////AP///wD///8A////AP///wD///8AAAD/EAAA/0AAAP9AAAD/cAAA/4AAAP+AAAD/gAAA/4AAAP+AAAD/QAAA/0AAAP8Q////AP///wD///8AAAD/YAAA//8AAP//AAD//wAA//8AAP//AAD//wAA//8AAP//AAD//wAA//8AAP//AAD//wAA/2D///8AAAD/MAAA//8AAP//AAD//wAA//8AAP//AAD//wAA//8AAP//AAD//wAA//8AAP//AAD//wAA//8AAP//AAD/MAAA/1AAAP//AAD//wAA//8AAP//AAD//wAA//8QEP//AAD//wAA//8AAP//AAD//wAA//8AAP//AAD//wAA/2AAAP+AAAD//wAA//8AAP//AAD//wAA//8AAP//4OD//1BQ//8AAP//AAD//wAA//8AAP//AAD//wAA//8AAP+AAAD/gAAA//8AAP//AAD//wAA//8AAP//AAD/////////////wMD//yAg//8AAP//AAD//wAA//8AAP//AAD/gAAA/4AAAP//AAD//wAA//8AAP//AAD//wAA/////////////7Cw//8gIP//AAD//wAA//8AAP//AAD//wAA/4AAAP+AAAD//wAA//8AAP//AAD//wAA//8AAP//4OD//0BA//8AAP//AAD//wAA//8AAP//AAD//wAA//8AAP+AAAD/UAAA//8AAP//AAD//wAA//8AAP//AAD//wAA//8AAP//AAD//wAA//8AAP//AAD//wAA//8AAP//AAD/YAAA/zAAAP//AAD//wAA//8AAP//AAD//wAA//8AAP//AAD//wAA//8AAP//AAD//wAA//8AAP//AAD//wAA/zD///8AAAD/YAAA//8AAP//AAD//wAA//8AAP//AAD//wAA//8AAP//AAD//wAA//8AAP//AAD//wAA/2D///8A////AP///wAAAP8QAAD/QAAA/0AAAP+AAAD/gAAA/4AAAP+AAAD/gAAA/4AAAP9AAAD/QAAA/xD///8A////AP///wD///8A////AP///wD///8A////AP///wD///8A////AP///wD///8A////AP///wD///8A////AP///wD///8A////AP///wD///8A////AP///wD///8A////AP///wD///8A////AP///wD///8A////AP///wD///8A//8AAP//AADAAwAAgAEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAgAEAAMADAAD//wAA//8AAA=="""
            ),
            thumbnail = Base64.decode("""""")
        )
    )
}

@OptIn(ExperimentalEncodingApi::class)
@Composable
@Preview
private fun UrlCardPreview(@PreviewParameter(UrlCardPreviewProvider::class) data: UrlCardData) {
    UrlCard(
        uri = "https://www.youtube.com/watch?v=DEhphcTaVxM",
        data = data
//            thumbnail = "https://i.ytimg.com/vi/DEhphcTaVxM/maxresdefault.jpg".toHttpUrlOrNull(),
//        )
    )
}

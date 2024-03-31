package fe.linksheet.experiment.url.bar

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import fe.linksheet.extension.compose.runIf
import fe.linksheet.ui.HkGroteskFontFamily
import io.github.fornewid.placeholder.foundation.PlaceholderHighlight
import io.github.fornewid.placeholder.material3.placeholder
import io.github.fornewid.placeholder.material3.shimmer
import me.saket.unfurl.UnfurlResult
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UrlCard(
    uri: String,
    unfurlResult: UnfurlResult?,
) {
    val context = LocalContext.current
    var showFullUrl by remember { mutableStateOf(false) }
//    var expanded by remember { mutableStateOf(true) }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardDefaults.shape)
            .combinedClickable(onClick = {}, onLongClick = {
                showFullUrl = !showFullUrl
            })
            .layoutId("url_card"),
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

//                    AsyncImage(model = , contentDescription = , imageLoader = )
//
                SubcomposeAsyncImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                        .testTag("thumbnail"),
                    model = ImageRequest.Builder(context).data(thumbnailUrl).build(),
                    alignment = Alignment.Center,
                    contentScale = ContentScale.FillWidth,
                    contentDescription = ""
                ) {
                    val state = painter.state
                    if (state is AsyncImagePainter.State.Success) {
                        SubcomposeAsyncImageContent(

                            modifier = Modifier.clip(CardDefaults.shape)
                        )
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
                    .runIf(!showFullUrl && unfurlResult == null) { it.height(60.dp) }
                    .padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (faviconUrl != null) {
//                        AsyncImage(
//                            model = ImageRequest.Builder(context).data(faviconUrl).size(16).build(),
//                            contentDescription = "",
//                            modifier = Modifier.border(1.dp, Color.Green),
//                        )

                    SubcomposeAsyncImage(
                        modifier = Modifier
                            .size(16.dp)
                            .testTag("favicon"),
                        model = faviconUrl,
                        contentDescription = ""
                    ) {
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
                    )
                }
            }
        }
    }
}

@Composable
@Preview
private fun UrlCardPreview() {
    UrlCard(
        uri = "https://www.youtube.com/watch?v=DEhphcTaVxM",
        unfurlResult = UnfurlResult(
            url = "https://www.youtube.com/watch?v=DEhphcTaVxM".toHttpUrlOrNull()!!,
            description = "Skip the waitlist and invest in blue-chip art for the very first time by signing up for Masterworks: https://www.masterworks.art/moonPurchase shares in great...",
            title = "What Happens When China Invades America?",
            favicon = "https://www.youtube.com/s/desktop/accca349/img/favicon.ico".toHttpUrlOrNull(),
            thumbnail = "https://i.ytimg.com/vi/DEhphcTaVxM/maxresdefault.jpg".toHttpUrlOrNull(),
        )
    )
}
